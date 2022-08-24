package apple.voltskiya.custom_mobs.leaps;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.pathfinder.DecodeMoveType;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.leaps.config.LeapDo;
import apple.voltskiya.custom_mobs.leaps.config.LeapPostConfig;
import apple.voltskiya.custom_mobs.leaps.config.LeapPreConfig;
import apple.voltskiya.custom_mobs.reload.PluginDisable;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.Location;
import voltskiya.apple.utilities.constants.TagConstants;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

public class GoalLeap extends Goal {
    protected final Mob me;
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
    public GoalLeap(Mob me, LeapPreConfig config, LeapPostConfig postConfig) {
        this.config = config;
        this.me = me;
        this.postConfig = postConfig;
        this.setMoveType(EnumSet.of(DecodeMoveType.JUMP.encode()));
        PluginDisable.addMob(me, this);
    }

    /**
     * @return whether I even want to consider checking whether to run
     */
    @Override
    public boolean a() {
        if (this.random.nextInt(config.getCheckInterval()) == 0 && !this.me.getBukkitEntity().getScoreboardTags().contains(TagConstants.IS_DOING_ABILITY)) {
            final Location themLocation = this.getGoalLocation();
            if (themLocation == null) return false;
            final Location meLocation = this.me.getBukkitEntity().getLocation();
            return (this.timeOfLastJump == null || DecodeEntity.getTicksLived(this.me) >= timeOfLastJump + this.config.getCooldown()) &&
                    (this.currentLeap == null || !this.currentLeap.isLeaping()) &&
                    this.config.isCorrectRange(meLocation, themLocation) &&
                    this.config.isValidPeak(meLocation, themLocation) &&
                    !this.postConfig.shouldStopCurrentLeap(null) &&
                    this.postConfig.isOnGround();
        } else {
            return false;
        }
    }


    /**
     * @return whether to keep running the pathfinder
     */
    @Override
    public boolean b() {
        // navigationAbstract.m() returns true if the entity is *not* navigating anywhere
        return this.a() && VoltskiyaPlugin.get().isEnabled() && (currentLeap == null || !currentLeap.isLeaping());
    }

    /**
     * @return something
     */
    @Override
    public boolean D_() {
        return false;
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
                this.currentLeap = new LeapDo(this.me, this::getGoalLocation, goalLocation, this.config, this.postConfig);
                // do the jump
                this.currentLeap.preLeap();
                this.timeOfLastJump = DecodeEntity.getTicksLived(this.me);
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
        return DecodeEntity.getHeight(me);
    }

    /**
     * @return a goal to jump to
     */
    @Nullable
    protected Location getGoalLocation() {
        final EntityLiving goalTarget = DecodeEntity.getLastTarget(this.me);
        if (goalTarget == null) return null;
        return goalTarget.getBukkitEntity().getLocation();
    }
}
