package apple.voltskiya.custom_mobs.abilities.common.micro_missile;

import apple.mc.utilities.item.material.MaterialUtils;
import apple.voltskiya.custom_mobs.abilities.tick.Tickable;
import apple.voltskiya.custom_mobs.util.ticking.HighFrequencyTick;
import apple.voltskiya.custom_mobs.util.ticking.TickGiverable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class MicroMissileManager implements Tickable {

    private static final Random random = new Random();
    private static MicroMissileManager instance;
    private final TickGiverable giver;
    private final ArrayList<MicroMissile> missiles = new ArrayList<>();
    private boolean isTicking = false;
    private long myTickerUid = -1;

    public MicroMissileManager() {
        instance = this;
        this.giver = HighFrequencyTick.get();
    }

    public static void shoot(Location spawnLocation, Location targetLocation, int count,
        MicroMissileShooter.MissileType missileType) {
        @Nullable RayTraceResult rayTrace = spawnLocation.getWorld().rayTraceBlocks(spawnLocation,
            targetLocation.toVector().subtract(spawnLocation.toVector()), 100,
            FluidCollisionMode.NEVER, true);
        if (rayTrace != null && rayTrace.getHitBlock() != null) {
            Block hitBlock = rayTrace.getHitBlock();
            targetLocation = hitBlock.getLocation();
        }

        get().giveMissile(
            new MicroMissile(spawnLocation, targetLocation, missileType.speed,
                missileType.minTicksToLive, missileType.damageAmount));
        for (int i = 0; i < count - 1; i++) {
            Location newTarget = new Location(targetLocation.getWorld(), targetLocation.getX(),
                targetLocation.getY(), targetLocation.getZ());
            newTarget.add((random.nextDouble() - 0.5) * MicroMissileConfig.get().variability,
                (random.nextDouble() - 0.5) * MicroMissileConfig.get().variability,
                (random.nextDouble() - 0.5) * MicroMissileConfig.get().variability);
            get().giveMissile(
                new MicroMissile(spawnLocation, newTarget, missileType.speed,
                    missileType.minTicksToLive, missileType.damageAmount));
        }
    }

    public static MicroMissileManager get() {
        return instance;
    }

    private void giveMissile(MicroMissile microMissile) {
        this.missiles.add(microMissile);
        if (!isTicking) {
            isTicking = true;
            this.myTickerUid = giver.add(this::tick);
        }
    }

    @Override
    public synchronized void tick() {
        Iterator<MicroMissile> vexUidIterator = missiles.iterator();
        boolean trim = false;
        while (vexUidIterator.hasNext()) {
            MicroMissile vexUid = vexUidIterator.next();
            if (vexUid == null || vexUid.isDead()) {
                // remove this vex D:
                vexUidIterator.remove();
                trim = true;
            } else {
                vexUid.tick();
            }
        }
        if (trim) {
            missiles.trimToSize();
            if (isTicking && missiles.isEmpty()) {
                giver.remove(myTickerUid);
                isTicking = false;
            }
        }
    }


    private static class MicroMissile {

        private final Location targetLocation;
        private final int ticksToLive;
        private final double speed;
        private final double damage;
        private final Vector velocity;
        private final Location location;
        private final Random random = new Random();
        private boolean isDead = false;
        private Vector acceleration = new Vector();
        private int ticksLived = 0;

        public MicroMissile(Location spawnLocation, Location targetLocation, double speed,
            int minTicksToLive, double damage) {
            this.speed = speed;
            this.damage = damage;
            this.velocity = targetLocation.toVector().subtract(spawnLocation.toVector()).normalize()
                .multiply(speed);
            this.location = new Location(spawnLocation.getWorld(), spawnLocation.getX(),
                spawnLocation.getY(), spawnLocation.getZ());
            this.targetLocation = targetLocation;
            this.ticksToLive =
                random.nextInt(MicroMissileConfig.get().additionalTicksToLive) + minTicksToLive;
            this.randomizeAcceleration();
        }

        private static void particles(Location location) {
            location.getWorld().spawnParticle(Particle.FLAME, location, 1, 0.01, 0.01, 0.01, 0.014);
            location.getWorld()
                .spawnParticle(Particle.SMOKE, location, 10, 0.04, 0.04, 0.04, 0.02);
        }

        public boolean isDead() {
            return isDead;
        }

        private synchronized void tick() {
            particles(location);
            if (this.ticksLived == this.ticksToLive)
                this.die();
            this.movementTick();
            if (!MaterialUtils.isWalkThroughable(location.getBlock().getType())) {
                Collection<Entity> nearbyEntities = location.getNearbyEntities(1, 1, 1);
                for (Entity nearby : nearbyEntities) {
                    if (nearby instanceof LivingEntity) {
                        damage((CraftLivingEntity) nearby);
                    }
                }
                this.die();
            } else {
                Collection<Entity> nearbyEntities = location.getNearbyEntities(1, 1, 1);
                for (Entity nearby : nearbyEntities) {
                    if (nearby instanceof Player) {
                        damage((CraftLivingEntity) nearby);
                        this.die();
                        break;
                    }
                }
            }
            ticksLived++;
        }

        private void damage(CraftLivingEntity nearby) {
            nearby.damage(damage);
        }

        private void movementTick() {
            this.location.add(this.velocity);

            this.velocity.add(this.acceleration);
            this.velocity.normalize().multiply(this.speed);

            if (this.ticksLived % 10 == 0) {
                this.randomizeAcceleration();
            }
        }

        private void randomizeAcceleration() {
            Vector goDirection = targetLocation.toVector().subtract(location.toVector());
            MicroMissileConfig config = MicroMissileConfig.get();
            goDirection.rotateAroundX(Math.toRadians(
                random.nextInt(config.randomAccelerationAngle * 2)
                    - config.randomAccelerationAngle));
            goDirection.rotateAroundZ(Math.toRadians(
                random.nextInt(config.randomAccelerationAngle * 2)
                    - config.randomAccelerationAngle));
            goDirection.rotateAroundY(Math.toRadians(
                random.nextInt(config.randomAccelerationAngle * 2)
                    - config.randomAccelerationAngle));
            this.acceleration = goDirection.normalize().multiply(config.accelerationSpeed);
        }

        private void die() {
            if (!this.isDead) {
                location.getWorld().spawnParticle(Particle.EXPLOSION, location, 0);
                location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 0.3f, 1.8f);
                this.isDead = true;
            }
        }
    }
}
