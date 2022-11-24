package apple.voltskiya.custom_mobs.leap.parent.config;

import apple.utilities.util.NumberUtils;
import apple.voltskiya.custom_mobs.leap.common.Kinematics;
import apple.voltskiya.custom_mobs.leap.parent.config.LeapMoveConfig;
import org.bukkit.Location;

public class LeapMath {

    private final LeapMoveConfig config;
    private final double minVelocityY;
    private final double maxVelocityY;
    private final double rangeBounds;
    private final double gravity;
    private final double minTheta;
    private final double maxTheta;
    private final double preferredVelocityY;
    private final double minPeakHeight;
    private final double minRange;

    public LeapMath(LeapMoveConfig config) {
        this.config = config;
        this.gravity = initGravity();
        this.minRange = initMinRange();
        this.minPeakHeight = initMinPeakHeight();
        this.maxTheta = initTheta(minRange(), maxPeakHeight());
        this.minTheta = initTheta(maxRange(), minPeakHeight());
        this.rangeBounds = this.maxRange() - this.minRange();
        this.minVelocityY = Kinematics.velocityInitial(0, gravity(), minPeakHeight());
        this.preferredVelocityY = Kinematics.velocityInitial(0, gravity(), preferredPeakHeight());
        this.maxVelocityY = Kinematics.velocityInitial(0, gravity(), maxPeakHeight());
    }

    // init calculations
    private double initMinRange() {
        return Math.max(.5, this.config.minRange);
    }

    private double initMinPeakHeight() {
        return Math.max(.5, this.config.minPeakHeight);
    }

    private double initTheta(double range, double height) {
        double velocityY = Math.sqrt(-2 * gravity() * height);
        double time = -velocityY - gravity();
        double velocityX = range / time;
        return Math.atan2(velocityX, velocityY);
    }

    private double initGravity() {
        return this.config.gravity / 20;
    }


    // calculated
    public double maxVelocityY() {
        return this.maxVelocityY;
    }

    public double minVelocityY() {
        return this.minVelocityY;
    }

    public double rangeBounds() {
        return this.rangeBounds;
    }

    public double preferredVelocityY() {
        return this.preferredVelocityY;
    }

    public double gravity() {
        return this.gravity;
    }

    public double maxTheta() {
        return this.maxTheta;
    }

    public double minTheta() {
        return this.minTheta;
    }

    public double minPeakHeight() {
        return this.minPeakHeight;
    }

    public double minRange() {
        return this.minRange;
    }

    // given by config
    public double maxPeakHeight() {
        return config.maxPeakHeight;
    }


    public double maxRange() {
        return this.config.maxRange;
    }

    public double preferredPeakHeight() {
        return this.config.preferredPeakHeight;
    }


    // utils
    public boolean estimateIsInRange(Location targetLocation, Location initialLocation) {
        double distanceX = targetLocation.getX() - initialLocation.getX();
        double distanceZ = targetLocation.getZ() - initialLocation.getZ();
        double distanceXZ = Kinematics.magnitude(distanceX, distanceZ);
        double distanceY = targetLocation.getY() - initialLocation.getY();

        return NumberUtils.betweenInclusiveDouble(this.minRange(), distanceXZ, this.maxRange()) && distanceY < this.maxPeakHeight();
    }
}
