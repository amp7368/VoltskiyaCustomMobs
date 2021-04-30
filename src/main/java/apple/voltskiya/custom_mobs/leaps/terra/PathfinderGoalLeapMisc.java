package apple.voltskiya.custom_mobs.leaps.terra;

import apple.voltskiya.custom_mobs.leaps.config.Leap;
import apple.voltskiya.custom_mobs.leaps.config.LeapConfig;
import net.minecraft.server.v1_16_R3.EntityHuman;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.PathfinderGoal;
import org.bukkit.util.Vector;

import java.util.EnumSet;
import java.util.Random;
import java.util.function.BooleanSupplier;

public class PathfinderGoalLeapMisc extends PathfinderGoal {
    private final EntityInsentient me;
    private final Random random = new Random();
    private final LeapConfig config;
    private final BooleanSupplier shouldStopCurrentLeap;
    private final BooleanSupplier isOnGround;
    private Vector foundCravedBlock;
    private boolean isLeaping = false;
    private Leap currentLeap = null;

    /**
     * find a block to navigate to
     *
     * @param me       the entity to navigate
     * @param config the config for the leap
     */
    public PathfinderGoalLeapMisc(EntityInsentient me,LeapConfig config, BooleanSupplier shouldStopCurrentLeap, BooleanSupplier isOnGround) {
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
//        return !this.me.getNavigation().m();
        final EntityLiving goalTarget = this.me.getGoalTarget();
        if (goalTarget == null) return false;
        if (this.config.isCorrectRange(this.me.getBukkitEntity().getLocation(), goalTarget.getBukkitEntity().getLocation())) {
            return true;
        }
        return false;
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
                this.isLeaping = true;
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
