package apple.voltskiya.custom_mobs.leaps.config;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import net.minecraft.world.entity.Mob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class LeapDo implements Runnable {
    public static final String NO_FALL_DAMAGE_TAG = "no_fall_damage";
    private final Entity bukkitEntity;
    private final Supplier<Location> getGoalLocation;
    private double gravity;
    private double yVelocityInitial;
    private double xVelocity;
    private final LeapPostConfig postConfig;
    private final Mob entity;
    private final LeapPreConfig preConfig;
    private double yVelocity;
    private double zVelocity;
    private int currentTime = 0;
    private boolean isLeaping = false;
    private boolean isMidJump = false;
    private final boolean alreadyHadNoFallDamage;

    public LeapDo(Mob entity, Supplier<Location> getGoalLocation, Location providedLocation, LeapPreConfig preConfig, LeapPostConfig postConfig) throws IllegalArgumentException {
        this.entity = entity;
        this.bukkitEntity = entity.getBukkitEntity();
        this.postConfig = postConfig;
        this.alreadyHadNoFallDamage = this.bukkitEntity.getScoreboardTags().contains(NO_FALL_DAMAGE_TAG);
        this.preConfig = preConfig;
        this.getGoalLocation = getGoalLocation;
        this.recalculate(providedLocation);
    }

    public void recalculate(Location goalLocation) {
        // get the distances
        Location nowLocation = this.bukkitEntity.getLocation();
        double xDistance = goalLocation.getX() - nowLocation.getX();
        double zDistance = goalLocation.getZ() - nowLocation.getZ();
        double xzDistance = Math.sqrt(xDistance * xDistance + zDistance * zDistance);

        // if the distance is less than 3, they probably mean to have the same time for all the given distances
        double fullTimeArcVariable;
        if (this.preConfig.getDistanceMin() < 3) {
            fullTimeArcVariable = this.preConfig.getTimeFullArc();
        } else {
            double xVelocity;
            xVelocity = this.preConfig.getDistanceMin() / this.preConfig.getTimeFullArc();
            fullTimeArcVariable = xzDistance / xVelocity;
        }

        // get the other info
        this.yVelocity = this.yVelocityInitial = 4 * this.preConfig.getPeak() / fullTimeArcVariable;
        this.xVelocity = xDistance / fullTimeArcVariable;
        this.zVelocity = zDistance / fullTimeArcVariable;
        this.gravity = -2 * this.yVelocityInitial / fullTimeArcVariable;
    }

    public void recalculate() {
        @Nullable final Location goalLocation = getGoalLocation.get();
        if (goalLocation != null)
            this.recalculate(goalLocation);
    }

    public void setLeaping(boolean leaping) {
        this.isLeaping = leaping;
    }

    public void preLeap() {
        this.isLeaping = true;
        this.postConfig.runPreLeap(entity, this);
    }

    public void leap() {
        bukkitEntity.addScoreboardTag(TagConstants.IS_DOING_ABILITY);
        bukkitEntity.setVelocity(new Vector(xVelocity, yVelocity, zVelocity));
        if (!this.alreadyHadNoFallDamage)
            this.bukkitEntity.addScoreboardTag(NO_FALL_DAMAGE_TAG);
        this.isMidJump = true;
        run();
    }

    @Override
    public void run() {
        if ((this.yVelocity <= 0 && this.postConfig.isOnGround())) {
            if (!alreadyHadNoFallDamage) this.bukkitEntity.removeScoreboardTag(NO_FALL_DAMAGE_TAG);
            this.isLeaping = false;
            this.isMidJump = false;
            bukkitEntity.removeScoreboardTag(TagConstants.IS_DOING_ABILITY);
            this.postConfig.runEnd(this.entity);
            return;
        }
        if (bukkitEntity.isDead() || this.postConfig.shouldStopCurrentLeap(this)) {
            if (!alreadyHadNoFallDamage) this.bukkitEntity.removeScoreboardTag(NO_FALL_DAMAGE_TAG);
            this.isMidJump = false;
            this.isLeaping = false;
            bukkitEntity.removeScoreboardTag(TagConstants.IS_DOING_ABILITY);
            this.postConfig.runInterrupted(this.entity);
            return;
        }
        // set the yVelocity to what it should
        yVelocity = yVelocityInitial + gravity * currentTime++;
        bukkitEntity.setVelocity(new Vector(xVelocity, yVelocity, zVelocity));
        try {
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this, 1);
        } catch (IllegalPluginAccessException ignored) {
        }
    }

    public boolean isLeaping() {
        return this.isLeaping;
    }

    public boolean isMidJump() {
        return this.isMidJump;
    }
}
