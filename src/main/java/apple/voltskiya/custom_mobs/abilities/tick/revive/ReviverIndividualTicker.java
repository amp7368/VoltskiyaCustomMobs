package apple.voltskiya.custom_mobs.abilities.tick.revive;

import apple.voltskiya.custom_mobs.util.DistanceUtils;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.sql.MobListSql;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftMob;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

public class ReviverIndividualTicker {
    private final ReviverManagerTicker.Closeness closeness;
    private boolean isReviving;
    private final ArrayList<Reviver> revivers = new ArrayList<>();
    private boolean isTicking = false;
    private final Random random = new Random();
    private long ticker;

    public ReviverIndividualTicker(ReviverManagerTicker.Closeness closeness) {
        this.closeness = closeness;
    }

    public synchronized void giveReviver(Reviver reviver) {
        this.revivers.add(reviver);
        if (!isTicking) {
            isTicking = true;
            this.ticker = closeness.getGiver().add(this::tick);
        }
    }

    private synchronized void tick() {
        boolean trim = false;
        Iterator<Reviver> reviverIterator = revivers.iterator();
        while (reviverIterator.hasNext()) {
            Reviver reviverObject = reviverIterator.next();
            UUID reviverUuid = reviverObject.getUniqueId();
            @Nullable Entity reviver = Bukkit.getEntity(reviverUuid);
            if (reviver == null || reviver.isDead()) {
                reviverObject.kill();
                MobListSql.removeMob(reviverUuid);
                reviverIterator.remove();
                trim = true;
                continue;
            }
            tickReviver(reviver, reviverObject);
            if (ReviverManagerTicker.get().amIGivingReviver(reviverObject, closeness)) {
                reviverIterator.remove();
                trim = true;
            }
        }
        if (trim) {
            if (revivers.isEmpty()) {
                isTicking = false;
                closeness.getGiver().remove(ticker);
            }
            revivers.trimToSize();
        }
    }

    private synchronized void tickReviver(Entity reviver, Reviver reviverObject) {
        if (isReviving) {
            if (random.nextDouble() < ReviverManagerTicker.get().REVIVE_CHANCE * closeness.getGiver().getTickSpeed()) {
                reviveGoal(reviver, reviverObject);
            }
        }
    }


    public void setIsReviving() {
        this.isReviving = true;
    }

    private synchronized void reviveGoal(Entity entity, Reviver reviverObject) {
        ReviveDeadManager.RecordedMob mobToRevive = ReviveDeadManager.get().reviveStart(entity.getLocation());
        if (mobToRevive != null && entity instanceof CraftMob) {
            CraftMob reviver = (CraftMob) entity;
            Location target = mobToRevive.getEntity().getLocation();
            new MoveToTarget(mobToRevive, target, reviver, reviverObject, 0).run();
        }
    }

    private static class MoveToTarget implements Runnable {
        private final ReviveDeadManager.RecordedMob reviveMe;
        private final Location target;
        private final CraftMob reviver;
        private final Reviver reviverObject;
        private final int count;
        private static final int MAX_COUNT = 200;

        public MoveToTarget(ReviveDeadManager.RecordedMob reviveMe, Location target, CraftMob reviver, Reviver reviverObject, int count) {
            this.reviveMe = reviveMe;
            this.target = target;
            this.reviver = reviver;
            this.reviverObject = reviverObject;
            this.count = count;
        }

        @Override
        public void run() {
            if (reviver.isDead()) {
                MobListSql.removeMob(reviver.getUniqueId());
                reviverObject.kill();
                return;
            }
            if (count == MAX_COUNT) return;
            double x = target.getX();
            double y = target.getY();
            double z = target.getZ();
            if (DistanceUtils.distance(reviver.getLocation(), target) > 1.5) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), new MoveToTarget(reviveMe, target, reviver, reviverObject, count + 1), 1);
            } else {
                ReviveDeadManager.get().reviveStart(reviveMe, reviver, reviverObject);
            }
            reviver.getHandle().getNavigation().a(x, y, z, 1.6f);
        }
    }
}
