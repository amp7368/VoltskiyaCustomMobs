package apple.voltskiya.custom_mobs.pathfinders.utilities;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.entity.DecodeNavigation;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.nms.parent.utility.NmsUtilityWrapper;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import apple.mc.utilities.world.vector.VectorUtils;

public class GoalMoveToTarget extends Goal {

    private final Location target;
    private final Mob me;
    private final int giveUpTick;
    private final Runnable callBack;
    private final double speed;
    private final NmsUtilityWrapper<Mob> wrapper;
    private boolean calledBack = false;

    public GoalMoveToTarget(Mob me, Location target, double speed, int giveUpTick, Runnable callBack) {
        this.me = me;
        this.wrapper = new NmsUtilityWrapper<>(this.me);
        this.target = target;
        this.giveUpTick = this.me.tickCount + giveUpTick;
        this.speed = speed;
        this.callBack = callBack;
    }

    /**
     * @return whether this pathfinder should be started
     */
    @Override
    public boolean a() {
        return this.me.tickCount < giveUpTick && VectorUtils.magnitude(this.me.getBukkitEntity().getLocation(), this.target) >= 1.25;
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
    public boolean D_() {
        return true;
    }

    /**
     * start the pathfinding
     */
    @Override
    public void c() {
    }

    @Override
    public void tick() {
        // go to the location
        this.wrapper.getNavigation().a(this.target.getX(), this.target.getY(), this.target.getZ(), speed);
    }

    /**
     * on completion of goal, do what?
     */
    @Override
    public void d() {
        // quit going to the location
        DecodeNavigation.cancelNavigation(this.wrapper.getNavigation());
        if (!calledBack) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), callBack);
            calledBack = true;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> this.me.goalSelector.a(this));
    }

}
