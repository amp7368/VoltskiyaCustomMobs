package apple.voltskiya.custom_mobs.leap.parent;

import apple.utilities.util.NumberUtils;
import apple.voltskiya.custom_mobs.leap.LeapModule;
import apple.voltskiya.custom_mobs.leap.common.Kinematics;
import apple.voltskiya.custom_mobs.leap.common.PlusOrMinus;
import apple.voltskiya.custom_mobs.leap.parent.config.LeapMath;
import apple.voltskiya.custom_mobs.leap.parent.config.LeapMoveConfig;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class LeapCalculation {

    private final LeapMath math;
    private final Location targetLocation;
    private final Location initialLocation;
    private boolean isPossible = false;
    private final Vector initialVelocity = new Vector();

    public LeapCalculation(Location targetLocation, Location initialLocation, LeapMoveConfig leap) {
        this.targetLocation = targetLocation;
        this.initialLocation = initialLocation;
        this.math = leap.math();
        this.initLeap();
    }

    private void initLeap() {
        double distanceX = targetLocation.getX() - initialLocation.getX();
        double distanceZ = targetLocation.getZ() - initialLocation.getZ();
        double distanceXZ = Kinematics.magnitude(distanceX, distanceZ);
        double distanceY = targetLocation.getY() - initialLocation.getY();

        // time could fail if the jump is impossible
        double minVelocityY = math.minVelocityY();
        double minVelocityYAdjust = Kinematics.timeVerifyVelocity(distanceY, minVelocityY, math.gravity());
        double maxVelocityY = math.maxVelocityY();
        double maxVelocityYAdjust = Kinematics.timeVerifyVelocity(distanceY, maxVelocityY, math.gravity());
        // check that minVelocityAdjust is less than maxVelocityY()
        if (minVelocityYAdjust > maxVelocityY || maxVelocityYAdjust < minVelocityY) {
            // fail
            return;
        }
        double minTime = Kinematics.time(distanceY, minVelocityYAdjust, math.gravity()).max();
        double maxTime = Kinematics.time(distanceY, maxVelocityYAdjust, math.gravity()).max();
        PlusOrMinus timeRange = new PlusOrMinus(minTime, maxTime);
        if (timeRange.isEitherFail()) {
            // can only happen with a rounding error
            LeapModule.get().logger()
                .error(String.format("TimeRange: '%s' failed with adjusted velocity <%f,%f>", timeRange, minTime, maxTime));
            return;
        }
        double preferredTime = Kinematics.time(distanceY, math.preferredVelocityY(), math.gravity()).max();
        double time;
        if (NumberUtils.betweenInclusiveDouble(timeRange.min(), preferredTime, timeRange.max())) {
            time = preferredTime;
        } else {
            double minDelta = Math.abs(timeRange.min() - preferredTime);
            double maxDelta = Math.abs(timeRange.max() - preferredTime);
            if (minDelta < maxDelta)
                time = timeRange.min();
            else
                time = timeRange.max();
        }

        double yVelocity = Kinematics.velocityInitialWithTime(distanceY, time, math.gravity());
        this.initialVelocity.setY(yVelocity);

        double velocityXZ = distanceXZ / time;
        double xzTheta = Math.atan2(distanceZ, distanceX);
        this.initialVelocity.setX(velocityXZ * Math.cos(xzTheta));
        this.initialVelocity.setZ(velocityXZ * Math.sin(xzTheta));
        this.isPossible = true;
    }


    public Vector initialVelocity() {
        return this.initialVelocity.clone();
    }

    public boolean isPossible() {
        return this.isPossible;
    }
}
