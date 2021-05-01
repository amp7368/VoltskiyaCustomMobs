package apple.voltskiya.custom_mobs.leaps.misc;

import apple.voltskiya.custom_mobs.leaps.config.LeapDo;
import apple.voltskiya.custom_mobs.leaps.config.LeapPostConfig;
import apple.voltskiya.custom_mobs.leaps.config.LeapPreConfig;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.PathfinderGoal;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

public class PathfinderGoalLeap extends PathfinderGoal {
    protected final EntityInsentient me;
    protected final Random random = new Random();
    protected final LeapPreConfig config;
    protected LeapPostConfig postConfig;
    protected LeapDo currentLeap = null;
    protected Integer timeOfLastJump = null;

    /**
     * find a block to navigate to
     *
     * @param me         the entity to navigate
     * @param config     the config for the leap
     * @param postConfig provides any runtime info for the leap
     */
    public PathfinderGoalLeap(EntityInsentient me, LeapPreConfig config, LeapPostConfig postConfig) {
        this.config = config;
        this.me = me;
        this.postConfig = postConfig;
        this.setMoveType(EnumSet.of(Type.JUMP));
    }

    /**
     * @return whether I even want to consider checking whether to run
     */
    @Override
    public boolean a() {
        if (this.random.nextInt(config.getCheckInterval()) == 0) {
            final Location themLocation = this.getGoalLocation();
            if (themLocation == null) return false;
            final Location meLocation = this.me.getBukkitEntity().getLocation();
            return (this.timeOfLastJump == null || this.me.ticksLived >= timeOfLastJump + this.config.getCooldown()) &&
                    (this.currentLeap == null || !this.currentLeap.isLeaping()) &&
                    this.config.isCorrectRange(meLocation, themLocation) &&
                    this.config.isValidPeak(meLocation, themLocation) &&
                    !this.postConfig.shouldStopCurrentLeap() &&
                    this.postConfig.isOnGround();

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
        return this.a();
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
        final Location meLocation = this.me.getBukkitEntity().getLocation();
        final Location goalLocation = this.getGoalLocation();
        if (goalLocation == null) return;
        this.config.randomizePeak();
        this.config.correctPeak(meLocation, goalLocation, this.getHitBoxHeight());
        if (this.config.isCorrectRange(meLocation, goalLocation) && this.config.isValidPeak(meLocation, goalLocation)) {
            try {
                this.currentLeap = new LeapDo(this.me, goalLocation, this.config, this.postConfig);
                // do the jump
                this.currentLeap.preLeap();
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

    /**
     * @return the height of me
     */
    protected double getHitBoxHeight() {
        return me.getHeight();
    }

    /**
     * @return a goal to jump to
     */
    @Nullable
    protected Location getGoalLocation() {
        final EntityLiving goalTarget = this.me.getGoalTarget();
        if (goalTarget == null) return null;
        return goalTarget.getBukkitEntity().getLocation();
    }
}
