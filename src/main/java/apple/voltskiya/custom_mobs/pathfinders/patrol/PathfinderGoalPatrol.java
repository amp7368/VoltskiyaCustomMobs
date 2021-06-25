package apple.voltskiya.custom_mobs.pathfinders.patrol;

import apple.voltskiya.custom_mobs.dungeon.patrols.Patrol;
import apple.voltskiya.custom_mobs.dungeon.patrols.PatrolStep;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSelector;

public class PathfinderGoalPatrol extends PathfinderGoal {
    private final Patrol patrol;
    private final EntityInsentient entity;
    private final PathfinderGoalSelector oldGoalSelector;
    private final PathfinderGoalSelector oldTargetSelector;

    public PathfinderGoalPatrol(Patrol patrol, EntityInsentient entity, PathfinderGoalSelector oldGoalSelector, PathfinderGoalSelector oldTargetSelector) {
        this.patrol = patrol;
        this.entity = entity;
        this.oldGoalSelector = oldGoalSelector;
        this.oldTargetSelector = oldTargetSelector;
    }

    /**
     * @return whether this pathfinder should be started
     */
    @Override
    public boolean a() {
        return true;
    }

    /**
     * @return true if we should keep running. otherwise false
     */
    @Override
    public boolean b() {
        return true;
    }

    /**
     * @return something
     */
    @Override
    public boolean C_() {
        return true;
    }

    /**
     * start the pathfinding
     */
    @Override
    public void c() {
//        patrol.setToClosestStep(entity);
    }

    /**
     * on completion of goal, do what?
     */
    @Override
    public void d() {
        // quit going to the location
    }

    /**
     * run the pathfinding
     */
    @Override
    public void e() {
        PatrolStep step = patrol.getCurrentStep();
//        step.doPatrol(entity);
    }
}
