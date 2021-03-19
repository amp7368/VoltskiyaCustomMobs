package apple.voltskiya.custom_mobs.heartbeat.tick.revive;

import apple.voltskiya.custom_mobs.DistanceUtils;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.heartbeat.tick.MobListSql;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftMob;
import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

public class ReviverIndividualTicker {
    private final ReviverManagerTicker.Closeness closeness;
    private boolean isReviving;
    private final ArrayList<UUID> revivers = new ArrayList<>();
    private boolean isTicking = false;
    private final Random random = new Random();
    private long ticker;

    public ReviverIndividualTicker(ReviverManagerTicker.Closeness closeness) {
        this.closeness = closeness;
    }

    public synchronized void giveReviver(Entity reviver) {
        this.revivers.add(reviver.getUniqueId());
        if (!isTicking) {
            isTicking = true;
            this.ticker = closeness.getGiver().add(this::tick);
        }
    }

    private synchronized void tick() {
        boolean trim = false;
        Iterator<UUID> reviverIterator = revivers.iterator();
        while (reviverIterator.hasNext()) {
            UUID reviverUuid = reviverIterator.next();
            @Nullable Entity reviver = Bukkit.getEntity(reviverUuid);
            if (reviver == null || reviver.isDead()) {
                MobListSql.removeMob(reviverUuid);
                reviverIterator.remove();
                trim = true;
                continue;
            }
            tickReviver(reviver);
            if (ReviverManagerTicker.get().amIGivingReviver(reviver, closeness)) {
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

    private synchronized void tickReviver(Entity reviver) {
        if (isReviving) {
            if (random.nextDouble() < ReviverManagerTicker.get().REVIVE_CHANCE * closeness.getGiver().getTickSpeed()) {
                reviveGoal(reviver);
            }
        }
    }


    public void setIsReviving() {
        this.isReviving = true;
    }

    private synchronized void reviveGoal(Entity entity) {
        ReviveDeadManager.RecordedMob mobToRevive = ReviveDeadManager.get().reviveStart(entity.getLocation());
        if (mobToRevive != null && entity instanceof CraftMob) {
            CraftMob reviver = (CraftMob) entity;
            Location target = mobToRevive.getEntity().getLocation();
            new MoveToTarget(mobToRevive, target, reviver, 0).run();
        }
    }

    private static class MoveToTarget implements Runnable {
        private final ReviveDeadManager.RecordedMob reviveMe;
        private final Location target;
        private final CraftMob reviver;
        private final int count;
        private static final int MAX_COUNT = 200;

        public MoveToTarget(ReviveDeadManager.RecordedMob reviveMe, Location target, CraftMob reviver, int count) {
            this.reviveMe = reviveMe;
            this.target = target;
            this.reviver = reviver;
            this.count = count;
        }

        @Override
        public void run() {
            if (reviver.isDead() || count == MAX_COUNT) {
                return;
            }
            int x = target.getBlockX();
            int y = target.getBlockY();
            int z = target.getBlockZ();
            PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 20, 1, false, false);
            reviver.addPotionEffect(speed);
            final PathEntity path = reviver.getHandle().getNavigation().a(new BlockPosition(x, y, z), 1);
            if (DistanceUtils.distance(reviver.getLocation(), target) > 2) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
                    new MoveToTarget(reviveMe, target, reviver, count + 1).run();
                }, 1);
            } else {
                ReviveDeadManager.get().reviveStart(reviveMe, reviver);
            }
            reviver.getHandle().getNavigation().a(path, 1f);
        }
    }
}
