package apple.voltskiya.custom_mobs.pathfinders.utilities;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.util.DistanceUtils;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.PathfinderGoal;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class PathfinderGoalMoveToTarget extends PathfinderGoal {
    private final Location target;
    private final EntityInsentient me;
    private final int giveUpTick;
    private final Runnable callBack;
    private final double speed;
    private boolean calledBack = false;

    public PathfinderGoalMoveToTarget(EntityInsentient me, Location target, double speed, int giveUpTick, Runnable callBack) {
        this.me = me;
        this.target = target;
        this.giveUpTick = me.ticksLived + giveUpTick;
        this.callBack = callBack;
        this.speed = speed;
    }

    /**
     * @return whether this pathfinder should be started
     */
    @Override
    public boolean a() {
        return this.me.ticksLived < giveUpTick && DistanceUtils.distance(this.me.getBukkitEntity().getLocation(), this.target) >= 0.5;
    }

    /**
     * @return true if we should keep running. otherwise false
     */
    @Override
    public boolean b() {
        return this.a();
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
    }

    /**
     * run the pathfinding
     */
    @Override
    public void e() {
        // go to the location
        this.me.getNavigation().a(this.target.getX(), this.target.getY(), this.target.getZ(), speed);
    }

    /**
     * on completion of goal, do what?
     */
    @Override
    public void d() {
        // quit going to the location
        this.me.getNavigation().o();
        if (!calledBack) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), callBack);
            calledBack = true;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> me.goalSelector.a(this));
    }

}
