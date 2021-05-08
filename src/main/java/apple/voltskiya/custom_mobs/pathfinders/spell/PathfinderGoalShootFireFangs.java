package apple.voltskiya.custom_mobs.pathfinders.spell;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.abilities.ai_changes.fire_fangs.FireFangs;
import apple.voltskiya.custom_mobs.util.DistanceUtils;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.PathfinderGoal;
import org.bukkit.Bukkit;

public class PathfinderGoalShootFireFangs extends PathfinderGoal {
    private final EntityInsentient me;
    private final FireFangs.FangsType type;
    private int lastShot;

    public PathfinderGoalShootFireFangs(EntityInsentient me, FireFangs.FangsType type) {
        this.me = me;
        this.type = type;
        this.lastShot = -type.getCooldown();
    }

    /**
     * @return whether this pathfinder should be started
     */
    @Override
    public boolean a() {
        return this.me.isAlive() &&
                this.me.ticksLived - lastShot >= type.getCooldown() &&
                this.me.getGoalTarget() != null &&
                DistanceUtils.distance(
                        this.me.getGoalTarget().getBukkitEntity().getLocation(),
                        this.me.getBukkitEntity().getLocation()
                ) <= type.getRange();
    }

    /**
     * always return false because we shouldn't run more than once
     *
     * @return true if we should keep running. otherwise false
     */
    @Override
    public boolean b() {
        return false;
    }

    /**
     * start the pathfinding
     */
    @Override
    public void c() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), type.construct(me));
        this.lastShot = this.me.ticksLived;
    }
}
