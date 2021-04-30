package apple.voltskiya.custom_mobs.leaps.misc;

import apple.voltskiya.custom_mobs.leaps.config.Leap;
import apple.voltskiya.custom_mobs.leaps.config.LeapConfig;
import net.minecraft.server.v1_16_R3.EntityHuman;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.PathfinderGoal;

import java.util.EnumSet;
import java.util.Random;
import java.util.function.BooleanSupplier;

public class PathfinderGoalLeap extends PathfinderGoal {
    protected final EntityInsentient me;
    protected final Random random = new Random();
    protected final LeapConfig config;
    protected final BooleanSupplier shouldStopCurrentLeap;
    protected final BooleanSupplier isOnGround;
    protected Leap currentLeap = null;

    /**
     * find a block to navigate to
     *
     * @param me                    the entity to navigate
     * @param config                the config for the leap
     * @param shouldStopCurrentLeap a function that will say if the mob should stop the leap at a random point
     *                              in the jump (ie. if the mob is hit)
     * @param isOnGround            a function to say whether the mob is on the ground or not
     */
    public PathfinderGoalLeap(EntityInsentient me, LeapConfig config, BooleanSupplier shouldStopCurrentLeap, BooleanSupplier isOnGround) {
        this.config = config;
        this.me = me;
        this.shouldStopCurrentLeap = shouldStopCurrentLeap;
        this.isOnGround = isOnGround;
        this.setMoveType(EnumSet.of(Type.JUMP));
    }

    /**
     * @return whether I even want to consider checking whether to run
     */
    @Override
    public boolean a() {
        if (this.random.nextInt(config.getCheckInterval()) == 0 &&
                this.me.getGoalTarget() instanceof EntityHuman) {
            final EntityLiving goalTarget = this.me.getGoalTarget();
            if (goalTarget == null) return false;
            return this.config.isCorrectRange(this.me.getBukkitEntity().getLocation(), goalTarget.getBukkitEntity().getLocation()) && !this.shouldStopCurrentLeap.getAsBoolean();
        } else {
            return false;
        }
    }


    /**
     * @return round 2 of whether I want to run ?
     */
    @Override
    public boolean b() {
        // navigationAbstract.m() returns true if the entity is *not* navigating anywhere
        final EntityLiving goalTarget = this.me.getGoalTarget();
        if (goalTarget == null) return false;
        return this.config.isCorrectRange(this.me.getBukkitEntity().getLocation(), goalTarget.getBukkitEntity().getLocation());
    }

    /**
     * todo does something I'm sure
     *
     * @return something
     */
    @Override
    public boolean C_() {
        return true;
    }

    /**
     * run the pathfinding
     */
    @Override
    public void c() {
        if (currentLeap != null && currentLeap.isLeaping()) {
            return;
        }
        final EntityLiving goalTarget = this.me.getGoalTarget();
        if (goalTarget == null) return;
        if (this.config.isCorrectRange(this.me.getBukkitEntity().getLocation(), goalTarget.getBukkitEntity().getLocation())) {
            try {
                this.currentLeap = new Leap(this.me.getBukkitEntity(), goalTarget.getBukkitEntity().getLocation(), this.config, this.shouldStopCurrentLeap, this.isOnGround);
                // do the jump
                this.currentLeap.leap();
            } catch (IllegalArgumentException ignored) {
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
        this.currentLeap = null;
    }

    /**
     * todo does something I'm sure
     */
    @Override
    public void e() {
    }

    public void setMoveType(EnumSet<Type> moveType) {
        super.a(moveType);
    }
}
