package apple.voltskiya.custom_mobs.pathfinders.utilities;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import voltskiya.apple.utilities.util.DistanceUtils;

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
        this.giveUpTick = DecodeEntity.getTicksLived(me) + giveUpTick;
        this.speed = speed;
        this.callBack = callBack;
    }

    /**
     * @return whether this pathfinder should be started
     */
    @Override
    public boolean a() {
        return DecodeEntity.getTicksLived(me) < giveUpTick && DistanceUtils.distance(this.me.getBukkitEntity().getLocation(), this.target) >= 1.25;
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
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> DecodeEntity.getGoalSelector(me).a(this));
    }

}
