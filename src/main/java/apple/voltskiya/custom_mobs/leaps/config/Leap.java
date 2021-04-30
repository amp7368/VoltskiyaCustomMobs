package apple.voltskiya.custom_mobs.leaps.config;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.function.BooleanSupplier;

public class Leap implements Runnable {
    public static final String NO_FALL_DAMAGE_TAG = "no_fall_damage";
    private final Entity entity;
    private final BooleanSupplier shouldStopCurrentLeap;
    private final BooleanSupplier isOnGround;
    private final double gravity;
    private final double yVelocityInitial;
    private final double xVelocity;
    private double yVelocity;
    private final double zVelocity;
    private int currentTime = 0;
    private boolean isLeaping = true;

    public Leap(CraftEntity entity, Location goalLocation, LeapConfig config, BooleanSupplier shouldStopCurrentLeap, BooleanSupplier isOnGround) throws IllegalArgumentException {
        this.entity = entity;
        this.shouldStopCurrentLeap = shouldStopCurrentLeap;
        this.isOnGround = isOnGround;

        // get the distances
        Location nowLocation = entity.getLocation();
        double xDistance = goalLocation.getX() - nowLocation.getX();
        double zDistance = goalLocation.getZ() - nowLocation.getZ();
        double xzDistance = Math.sqrt(xDistance * xDistance + zDistance * zDistance);
        double xVelocity = config.getDistanceMin() / config.getTimeFullArc();
        double fullTimeArcVariable = xzDistance / xVelocity;

        // get the other info
        this.yVelocity = this.yVelocityInitial = 4 * config.getPeak() / fullTimeArcVariable;
        this.xVelocity = xDistance / fullTimeArcVariable;
        this.zVelocity = zDistance / fullTimeArcVariable;
        this.gravity = -2 * this.yVelocityInitial / fullTimeArcVariable;
    }


    public void leap() {
        entity.setVelocity(new Vector(xVelocity, yVelocity, zVelocity));
        this.isLeaping = true;
        this.entity.addScoreboardTag(NO_FALL_DAMAGE_TAG);
        run();
    }

    @Override
    public void run() {
        if (entity.isDead() ||
                shouldStopCurrentLeap.getAsBoolean() ||
                (this.yVelocity <= 0 && this.isOnGround.getAsBoolean())) {
            this.entity.removeScoreboardTag(NO_FALL_DAMAGE_TAG);
            this.isLeaping = false;
            return;
        }
        // set the yVelocity to what it should
        yVelocity = yVelocityInitial + gravity * currentTime++;
        entity.setVelocity(new Vector(xVelocity, yVelocity, zVelocity));

        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this, 1);
    }

    public boolean isLeaping() {
        return this.isLeaping;
    }
}
