package apple.voltskiya.custom_mobs.pathfinders;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.disable.PluginDisable;
import apple.voltskiya.custom_mobs.util.DistanceUtils;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.PathfinderGoal;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;

import java.util.EnumSet;
import java.util.Random;

public class PathfinderGoalApproachSlowly extends PathfinderGoal {
    public static final int CHECK_INTERVAL = 80;
    private final EntityInsentient me;
    private final Runnable runAfterClose;
    private final Random random = new Random();
    private final double speed;
    private final double approachedDistance;
    private boolean isRunning = false;

    /**
     * find a block to navigate to
     *
     * @param me the entity to navigate
     */
    public PathfinderGoalApproachSlowly(EntityInsentient me, double speed, double approachedDistance, Runnable runAfterClose) {
        this.me = me;
        this.speed = speed;
        this.runAfterClose = runAfterClose;
        this.approachedDistance = approachedDistance;
        this.setMoveType(EnumSet.of(Type.MOVE));
        PluginDisable.addMob(me, this);
    }

    /**
     * @return whether this pathfinder should be started
     */
    @Override
    public boolean a() {
        if (this.random.nextInt(CHECK_INTERVAL) == 0) {
            final EntityLiving goalTarget = this.me.getGoalTarget();
            if (goalTarget instanceof EntityPlayer && this.isCorrectDistance(goalTarget) && ((EntityPlayer) goalTarget).getBukkitEntity().getGameMode() == GameMode.SURVIVAL) {
                this.isRunning = true;
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * @return true if we should keep running. otherwise false
     */
    @Override
    public boolean b() {
        return this.isRunning;
    }

    /**
     * @return something
     */
    @Override
    public boolean C_() {
        return true;
    }


    private boolean isCorrectDistance(EntityLiving goalTarget) {
        boolean isCorrect = this.approachedDistance < DistanceUtils.magnitude(goalTarget.locX() - this.me.locX(), goalTarget.locY() - this.me.locY(), goalTarget.locZ() - this.me.locZ());
        if (!isCorrect) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), runAfterClose);
        }
        return isCorrect;
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
        final EntityLiving goalTarget = this.me.getGoalTarget();
        if (goalTarget == null) {
            this.isRunning = false;
        } else {
            if (isCorrectDistance(goalTarget)) {
                this.me.getNavigation().a(goalTarget.locX(), goalTarget.locY(), goalTarget.locZ(), speed);
            } else {
                this.me.getNavigation().o();
                this.isRunning = false;
            }
        }
    }

    /**
     * on completion of goal, do what?
     */
    @Override
    public void d() {
        // quit going to the location
        this.me.getNavigation().o();
    }

    public void setMoveType(EnumSet<Type> moveType) {
        super.a(moveType);
    }
}
