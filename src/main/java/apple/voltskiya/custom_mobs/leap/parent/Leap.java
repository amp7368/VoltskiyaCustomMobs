package apple.voltskiya.custom_mobs.leap.parent;

import apple.voltskiya.mob_manager.mob.MMSpawned;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public abstract class Leap<Config extends LeapConfig> extends LeapStage<Config> {

    private final Vector velocity;

    public Leap(MMSpawned mob, Config config, Location targetLocation) {
        super(mob, config, config.leap, targetLocation);
        this.velocity = initLeap();
    }

    private Vector initLeap() {
        Location mobLocation = getLocation();
        double a = targetLocation.getX() - mobLocation.getX();
        double b = targetLocation.getZ() - mobLocation.getZ();
        double distanceXZ = Math.sqrt(a * a + b * b);
        double distanceY = targetLocation.getY() - mobLocation.getY();
        double velocityInitial = fullConfig.velocity;
        double c = fullConfig.gravity * distanceXZ / (2 * velocityInitial * velocityInitial);
        double computed = 1 - 4 * c * (c - distanceY / distanceXZ);
        if (computed < 0 || Double.isNaN(computed)) {
            this.cancel();
            return new Vector();
        }
        // this could be a -1 - Math.sqrt() as well
        double theta = Math.atan((-1 + Math.sqrt(computed)) / (2 * c));
        double velocityXZ = velocityInitial * Math.cos(theta);
        double velocityY = velocityInitial * Math.sin(theta);

        double xzTheta = Math.atan2(b, a);
        double velocityX = velocityXZ * Math.cos(xzTheta);
        double velocityZ = velocityXZ * Math.sin(xzTheta);
        return new Vector(velocityX, velocityY, velocityZ);
    }

    public void tick() {
        getMob().setVelocity(this.velocity);
        this.velocity.add(new Vector(0, fullConfig.gravity, 0));
    }

    protected boolean isFinished() {
        return super.isFinished() || isOnGround();
    }

    private boolean isOnGround() {
        return mob.getMob().isOnGround() && this.tick >= 5;
    }

}
