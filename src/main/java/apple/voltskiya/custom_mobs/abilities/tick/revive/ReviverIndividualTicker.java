package apple.voltskiya.custom_mobs.abilities.tick.revive;

import apple.voltskiya.custom_mobs.pathfinders.utilities.PathfinderGoalMoveToTarget;
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
    public static final int GIVE_UP_MOVE_TICK = 200;
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
        RecordedMob mobToRevive = ReviveDeadManager.get().reviveStart(entity.getLocation());
        if (mobToRevive != null && entity instanceof CraftMob) {
            CraftMob reviver = ((CraftMob) entity);
            Location target = mobToRevive.getEntity().getLocation();
            reviver.getHandle().goalSelector.a(-1, new PathfinderGoalMoveToTarget(reviver.getHandle(), target, 1.6, GIVE_UP_MOVE_TICK, () -> {
                if (mobToRevive.isNearby(reviver.getLocation())) {
                    ReviveDeadManager.get().reviveStart(mobToRevive, reviver, reviverObject);
                }
            }));
        }
    }
}
