package apple.voltskiya.custom_mobs.leaps.config;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class Leap implements Runnable {
    public static final String NO_FALL_DAMAGE_TAG = "no_fall_damage";
    private final Entity bukkitEntity;
    private final double gravity;
    private final double yVelocityInitial;
    private final double xVelocity;
    private final LeapPostConfig postConfig;
    private final EntityInsentient entity;
    private double yVelocity;
    private final double zVelocity;
    private int currentTime = 0;
    private boolean isLeaping = false;
    private final boolean alreadyHadNoFallDamage;

    public Leap(EntityInsentient entity, Location goalLocation, LeapPreConfig config, LeapPostConfig postConfig) throws IllegalArgumentException {
        this.entity = entity;
        this.bukkitEntity = entity.getBukkitEntity();
        this.postConfig = postConfig;
        this.alreadyHadNoFallDamage = this.bukkitEntity.getScoreboardTags().contains(NO_FALL_DAMAGE_TAG);
        // get the distances
        Location nowLocation = this.bukkitEntity.getLocation();
        double xDistance = goalLocation.getX() - nowLocation.getX();
        double zDistance = goalLocation.getZ() - nowLocation.getZ();
        double xzDistance = Math.sqrt(xDistance * xDistance + zDistance * zDistance);

        // if the distance is less than 3, they probably mean to have the same time for all the given distances
        double fullTimeArcVariable;
        if (config.getDistanceMin() < 3) {
            fullTimeArcVariable = config.getTimeFullArc();
        } else {
            double xVelocity;
            xVelocity = config.getDistanceMin() / config.getTimeFullArc();
            fullTimeArcVariable = xzDistance / xVelocity;
        }

        // get the other info
        this.yVelocity = this.yVelocityInitial = 4 * config.getPeak() / fullTimeArcVariable;
        this.xVelocity = xDistance / fullTimeArcVariable;
        this.zVelocity = zDistance / fullTimeArcVariable;
        this.gravity = -2 * this.yVelocityInitial / fullTimeArcVariable;
    }


    public void preLeap() {
        this.postConfig.runPreLeap(entity, this::leap);
    }

    public void leap() {
        bukkitEntity.setVelocity(new Vector(xVelocity, yVelocity, zVelocity));
        this.isLeaping = true;
        if (!this.alreadyHadNoFallDamage)
            this.bukkitEntity.addScoreboardTag(NO_FALL_DAMAGE_TAG);
        run();
    }

    @Override
    public void run() {
        if ((this.yVelocity <= 0 && this.postConfig.isOnGround())) {
            if (!alreadyHadNoFallDamage) this.bukkitEntity.removeScoreboardTag(NO_FALL_DAMAGE_TAG);
            this.isLeaping = false;
            this.postConfig.runEnd(this.entity);
            return;
        }
        if (bukkitEntity.isDead() || this.postConfig.shouldStopCurrentLeap()) {
            if (!alreadyHadNoFallDamage) this.bukkitEntity.removeScoreboardTag(NO_FALL_DAMAGE_TAG);
            this.isLeaping = false;
            this.postConfig.runInterrupted(this.entity);
            return;
        }
        // set the yVelocity to what it should
        yVelocity = yVelocityInitial + gravity * currentTime++;
        bukkitEntity.setVelocity(new Vector(xVelocity, yVelocity, zVelocity));

        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this, 1);
    }

    public boolean isLeaping() {
        return this.isLeaping;
    }
}
