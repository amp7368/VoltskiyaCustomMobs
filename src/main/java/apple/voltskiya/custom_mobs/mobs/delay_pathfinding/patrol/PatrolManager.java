package apple.voltskiya.custom_mobs.mobs.delay_pathfinding.patrol;

import apple.voltskiya.custom_mobs.mobs.nms.parent.register.RegisteredEntityEater;
import net.minecraft.world.entity.EntityInsentient;

public class PatrolManager implements RegisteredEntityEater {
    public static final String PATROL_TAG = "patrol";

    public PatrolManager() {

    }

    /**
     * eat an entity
     * please override one of the eatEntity events
     * (there's 3 cause it makes things easier and lets the programmer choose what they need)
     *
     * @param entity the entity to eat
     */
    @Override
    public void eatEntity(EntityInsentient entity) {
        for (String tag : entity.getScoreboardTags()) {
            String[] tagSplit = tag.split("\\.");
            if (tagSplit[0].equals(PATROL_TAG) && tagSplit.length == 5) {
//                Dungeon dungeon = DungeonActive.getDungeon(tagSplit[3]);
//                @Nullable Patrol patrol = dungeon.getPatrol(tagSplit[4]);
//                if (patrol != null) {
//                    Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
//                        // remove old pathfinding by replacing it
//                        PathfinderGoalSelector oldGoalSelector = entity.goalSelector;
//                        entity.goalSelector = new PathfinderGoalSelector(entity.getWorld().getMethodProfilerSupplier());
//                        PathfinderGoalSelector oldTargetSelector = entity.targetSelector;
//                        entity.targetSelector = new PathfinderGoalSelector(entity.getWorld().getMethodProfilerSupplier());
//                        entity.goalSelector.a(0, new PathfinderGoalPatrol(patrol, entity, oldGoalSelector, oldTargetSelector));
//                        addMob(entity.getUniqueID());
//                    }, 0);
//                }
            }
        }
    }

    /**
     * @return the name of this mob type
     */
    @Override
    public String getName() {
        return "patrol";
    }
}
