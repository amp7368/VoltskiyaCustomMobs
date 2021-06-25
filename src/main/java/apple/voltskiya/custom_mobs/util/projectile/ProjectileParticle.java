package apple.voltskiya.custom_mobs.util.projectile;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.util.VectorUtils;
import apple.voltskiya.custom_mobs.util.minecraft.MaterialUtils;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class ProjectileParticle implements Runnable {
    private final static Vector acceleration = new Vector(0, -0.02, 0);
    protected final Random random = new Random();
    protected final Location location;
    protected final double radius;
    protected final List<Particle> particles;
    protected final int particlesLength;
    protected final TriConsumer<Particle, Location, Vector> doParticleFinal;
    protected final Consumer<Location> finishedCallback;
    private final int numOfParticles;
    protected Vector direction;
    protected int tick = 0;

    public ProjectileParticle(Vector direction, Location location, double radius, List<Particle> particles, Consumer<Location> finishedCallback, int numOfParticles) {
        this.direction = direction;
        this.location = location;
        this.radius = radius;

        this.particles = particles;
        this.particlesLength = particles.size();
        this.finishedCallback = finishedCallback;
        this.numOfParticles = numOfParticles;
        this.doParticleFinal = this::doParticle;
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this);
    }

    protected void doParticle(Particle particle, Location location, Vector xyz) {
        location.getWorld().spawnParticle(particle, xyz.getX() + location.getX(), xyz.getY() + location.getY(), xyz.getZ() + location.getZ(), 0);
    }

    public ProjectileParticle(Vector direction, Location location, double radius, List<Particle> particles, TriConsumer<Particle, Location, Vector> doParticleFinal, Consumer<Location> finishedCallback, int numOfParticles) {
        this.direction = direction;
        this.location = location;
        this.radius = radius;

        this.particles = particles;
        this.particlesLength = particles.size();
        this.finishedCallback = finishedCallback;
        this.doParticleFinal = doParticleFinal;
        this.numOfParticles = numOfParticles;
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this);
    }

    @Override
    public void run() {
        tick++;
        doParticles();

        boolean shouldContinue = movementTick();
        if (shouldContinue) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this, 1);
        } else {
            finishedCallback.accept(location);
        }
    }

    protected void doParticles() {
        for (int i = 0; i < this.numOfParticles; i++) {
            double theta = random.nextDouble() * 360;
            double thetay = random.nextDouble() * 360;
            double x = Math.cos(Math.toRadians(theta)) * this.radius;
            double y = Math.sin(Math.toRadians(theta)) * this.radius;
            double z = Math.sin(Math.toRadians(thetay)) * this.radius;
            this.doParticleFinal.accept(particles.get(random.nextInt(particlesLength)), location, new Vector(x, y, z));
        }
    }

    protected boolean movementTick() {
        direction.add(acceleration);
        return addDirectionStepped();
    }

    protected boolean addDirectionStepped() {
        Vector bigDirection = direction.clone();
        Vector smallDirection = direction.normalize().multiply(.5);
        double smallSize = VectorUtils.magnitude(smallDirection);
        boolean shouldContinue = true;
        while (VectorUtils.magnitude(bigDirection) > smallSize) {
            location.add(smallDirection);
            bigDirection.subtract(smallDirection);
            if (shouldContinue) shouldContinue = this.shouldContinue();
            else break;
        }
        location.add(bigDirection);
        if (shouldContinue) shouldContinue = this.shouldContinue();
        return shouldContinue;
    }

    protected boolean shouldContinue() {
        World world = location.getWorld();
        for (Player p : location.getNearbyEntitiesByType(Player.class, 1, 1, 1)) {
            BoundingBox b = p.getBoundingBox();
            if (b.contains(location.toVector())) return false;
        }
        return MaterialUtils.isPassable(world.getBlockAt(location).getType());
    }
}
