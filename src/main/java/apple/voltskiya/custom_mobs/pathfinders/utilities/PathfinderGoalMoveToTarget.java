package apple.voltskiya.custom_mobs.pathfinders.utilities;

import apple.mc.utilities.world.vector.VectorUtils;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class PathfinderGoalMoveToTarget extends Goal {

    private final Location target;
    private final Mob me;
    private final int giveUpTick;
    private final Runnable callBack;
    private final int speed;
    private boolean calledBack = false;

    public PathfinderGoalMoveToTarget(Mob me, Location target, int speed, int giveUpTick, Runnable callBack) {
        this.me = me;
        this.target = target;
        this.giveUpTick = this.me.tickCount + giveUpTick;
        this.speed = speed;
        this.callBack = callBack;
    }

    /**
     * @return whether this pathfinder should be started
     */
    @Override
    public boolean canUse() {
        return this.me.tickCount < giveUpTick
            && VectorUtils.distance(this.me.getBukkitEntity().getLocation(), this.target) >= 1.25;
    }

    /**
     * @return true if we should keep running. otherwise false
     */
    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void start() {
        Path path = this.me.getNavigation().createPath(this.target.getX(), this.target.getY(), this.target.getZ(), speed);
        this.me.getNavigation().moveTo(path, this.speed);
    }

    /**
     * on completion of goal, do what?
     */
    @Override
    public void stop() {
        // quit going to the location
        this.me.getNavigation().stop();
        if (!calledBack) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), callBack);
            calledBack = true;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> this.me.goalSelector.removeGoal(this));
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        // go to the location
        this.me.getNavigation().recomputePath();
    }

}
