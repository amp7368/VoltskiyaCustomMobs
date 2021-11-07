package apple.voltskiya.custom_mobs.trash.revive;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.mob.MobReviver;
import apple.voltskiya.custom_mobs.pathfinders.utilities.PathfinderGoalMoveToTarget;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftMob;
import org.bukkit.entity.Entity;
import voltskiya.apple.utilities.util.DistanceUtils;
import voltskiya.apple.utilities.util.constants.TagConstants;

import java.util.ArrayList;
import java.util.Random;

public class OldReviverIndividualTicker {
    public static final int GIVE_UP_MOVE_TICK = 200;
    private final OldReviverManagerTicker.Closeness closeness;
    private boolean isReviving;
    private final ArrayList<MobReviver> revivers = new ArrayList<>();
    private boolean isTicking = false;
    private final Random random = new Random();
    private long ticker;

    public OldReviverIndividualTicker(OldReviverManagerTicker.Closeness closeness) {
        this.closeness = closeness;
    }

    public synchronized void giveReviver(MobReviver reviver) {
        this.revivers.add(reviver);
        if (!isTicking) {
            isTicking = true;
            this.ticker = closeness.getGiver().add(this::tick);
        }
    }

    private synchronized void tick() {
//        boolean trim = false;
//        Iterator<MobReviver> reviverIterator = revivers.iterator();
//        while (reviverIterator.hasNext()) {
//            MobReviver reviverObject = reviverIterator.next();
//            @Nullable Entity reviver = reviverObject.getEntity();
//            if (reviver == null || reviver.isDead()) {
//                reviverObject.kill();
//                MobListSql.removeMob(reviverObject.getUniqueId());
//                reviverIterator.remove();
//                trim = true;
//                continue;
//            }
//            tickReviver(reviver, reviverObject);
//            if (OldReviverManagerTicker.get().amIGivingReviver(reviverObject, closeness)) {
//                reviverIterator.remove();
//                trim = true;
//            }
//        }
//        if (trim) {
//            if (revivers.isEmpty()) {
//                isTicking = false;
//                closeness.getGiver().remove(ticker);
//            }
//            revivers.trimToSize();
//        }
    }

    private synchronized void tickReviver(Entity reviver, MobReviver reviverObject) {
        if (isReviving) {
            if (random.nextDouble() < OldReviverManagerTicker.get().REVIVE_CHANCE * closeness.getGiver().getTickSpeed()) {
                reviveGoal(reviver, reviverObject);
            }
        }
    }


    public void setIsReviving() {
        this.isReviving = true;
    }

    private synchronized void reviveGoal(Entity entity, MobReviver reviverObject) {
        OldRecordedMob mobToRevive = OldReviveDeadManager.get().reviveStart(entity.getLocation(), reviverObject);
        if (mobToRevive != null && entity instanceof CraftMob reviver) {
            Location target = mobToRevive.getEntity().getLocation();
            reviver.addScoreboardTag(TagConstants.isDoingAbility);
            DecodeEntity.getGoalSelector(reviver.getHandle()).a(-1, new PathfinderGoalMoveToTarget(reviver.getHandle(), target, 1.6, GIVE_UP_MOVE_TICK, () -> {
                if (DistanceUtils.distance(mobToRevive.location, reviver.getLocation()) <= 1) {
                    OldReviveDeadManager.get().reviveStart(mobToRevive, reviver, reviverObject);
                }
            }));
        }
    }
}
