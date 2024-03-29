package apple.voltskiya.custom_mobs.util.projectile;

import java.util.List;
import java.util.function.Consumer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public class ProjectileParticleMissle extends ProjectileParticle {

    private static final int MIN_TICKS_TO_LIVE = 100;
    private static final int MAX_ADDITIONAL_TICKS_TO_LIVE = 30;
    private static final double SHOT_SPEED = .9;
    private static final int RANDOM_ACCELERATION_ANGLE = 20;
    private static final double ACCELERATION_SPEED = .3;
    private final Location targetLocation;
    private final int tickToLiveTo =
        random.nextInt(MAX_ADDITIONAL_TICKS_TO_LIVE) + MIN_TICKS_TO_LIVE;
    private Vector acceleration = null;

    public ProjectileParticleMissle(Location location, Location targetLocation, Vector direction,
        List<Particle> particles, double radius, Consumer<Location> finishedCallback,
        int numOfParticles) {
        super(direction, location, radius, particles, finishedCallback, numOfParticles);
        this.targetLocation = targetLocation;

        randomizeAcceleration();
    }

    private void randomizeAcceleration() {
        Vector goDirection = this.targetLocation.toVector().subtract(this.location.toVector());
        goDirection.rotateAroundX(Math.toRadians(
            random.nextInt(RANDOM_ACCELERATION_ANGLE * 2) - RANDOM_ACCELERATION_ANGLE));
        goDirection.rotateAroundZ(Math.toRadians(
            random.nextInt(RANDOM_ACCELERATION_ANGLE * 2) - RANDOM_ACCELERATION_ANGLE));
        goDirection.rotateAroundY(Math.toRadians(
            random.nextInt(RANDOM_ACCELERATION_ANGLE * 2) - RANDOM_ACCELERATION_ANGLE));
        this.acceleration = goDirection.normalize().multiply(ACCELERATION_SPEED);
    }

    @Override
    protected boolean movementTick() {
        if (this.acceleration == null)
            this.acceleration = new Vector(0, 0, 0);
        if (this.tick % 5 == 0)
            randomizeAcceleration();
        direction.add(acceleration);

        // keep hold of the real direction. the smaller direction is just for adding the step
        Vector realDirection = direction;
        direction = direction.clone().normalize().multiply(SHOT_SPEED);
        final boolean shouldContinue = addDirectionStepped();
        direction = realDirection;
        return shouldContinue;
    }

    @Override
    protected boolean shouldContinue() {
        return super.shouldContinue() && this.tick < tickToLiveTo;
    }
}
