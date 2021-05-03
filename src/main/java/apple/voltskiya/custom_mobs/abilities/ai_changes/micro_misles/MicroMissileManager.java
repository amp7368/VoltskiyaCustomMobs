package apple.voltskiya.custom_mobs.abilities.ai_changes.micro_misles;

import apple.voltskiya.custom_mobs.abilities.tick.Tickable;
import apple.voltskiya.custom_mobs.ticking.HighFrequencyTick;
import apple.voltskiya.custom_mobs.ticking.TickGiverable;
import apple.voltskiya.custom_mobs.util.VectorUtils;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class MicroMissileManager implements Tickable {
    private static MicroMissileManager instance;
    private final TickGiverable giver;
    private final ArrayList<MicroMissile> missiles = new ArrayList<>();
    private boolean isTicking = false;
    private long myTickerUid = -1;
    private static final Random random = new Random();

    public MicroMissileManager() {
        instance = this;
        this.giver = HighFrequencyTick.get();
    }

    public static void shoot(Location spawnLocation, Location targetLocation) {
        @Nullable RayTraceResult rayTrace = spawnLocation.getWorld().rayTraceBlocks(spawnLocation, targetLocation.toVector().subtract(spawnLocation.toVector()), 100, FluidCollisionMode.NEVER, true);
        if (rayTrace != null && rayTrace.getHitBlock() != null) {
            Block hitBlock = rayTrace.getHitBlock();
            targetLocation = hitBlock.getLocation();
        }
        get().giveMissile(new MicroMissile(spawnLocation, targetLocation));
        for (int i = 0; i < 5; i++) {
            Location newTarget = new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY(), targetLocation.getZ());
            newTarget.add((random.nextDouble() - 0.5) * MicroMissleSpawnManager.VARIABLITY, (random.nextDouble() - 0.5) * MicroMissleSpawnManager.VARIABLITY, (random.nextDouble() - 0.5) * MicroMissleSpawnManager.VARIABLITY);
            get().giveMissile(new MicroMissile(spawnLocation, newTarget));
        }
    }

    private void giveMissile(MicroMissile microMissile) {
        this.missiles.add(microMissile);
        if (!isTicking) {
            isTicking = true;
            this.myTickerUid = giver.add(this::tick);
        }
    }

    public static MicroMissileManager get() {
        return instance;
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
        private boolean isDead = false;
        private Vector acceleration = new Vector();
        private final Vector velocity;
        private final Location location;
        private int ticksLived = 0;
        private final Random random = new Random();

        public MicroMissile(Location spawnLocation, Location targetLocation) {
            this.velocity = targetLocation.toVector().subtract(spawnLocation.toVector()).normalize().multiply(MicroMissleSpawnManager.SPEED);
            this.location = new Location(spawnLocation.getWorld(), spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ());
            this.targetLocation = targetLocation;
            this.ticksToLive = random.nextInt(MicroMissleSpawnManager.ADDITIONAL_TICKS_TO_LIVE) + MicroMissleSpawnManager.MIN_TICKS_TO_LIVE;
            this.randomizeAcceleration();
        }

        public boolean isDead() {
            return isDead;
        }

        private synchronized void tick() {
            particles(location);
            if (this.ticksLived == this.ticksToLive) this.die();
            this.movementTick();
            if (location.getBlock().getType().isSolid()) {
                Collection<Entity> nearbyEntities = location.getNearbyEntities(1, 1, 1);
                for (Entity nearby : nearbyEntities) {
                    if (nearby instanceof LivingEntity) {
                        location.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, location, 0);
                        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                        ((LivingEntity) nearby).damage(MicroMissleSpawnManager.get().DAMAGE_AMOUNT);
                    }
                }
                this.die();
            } else {
                Collection<Entity> nearbyEntities = location.getNearbyEntities(1, 1, 1);
                for (Entity nearby : nearbyEntities) {
                    if (nearby instanceof Player) {
                        location.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, location, 0);
                        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                        ((LivingEntity) nearby).damage(MicroMissleSpawnManager.get().DAMAGE_AMOUNT);
                        this.die();
                        break;
                    }
                }
            }
            ticksLived++;
        }

        private void movementTick() {
            System.out.println(VectorUtils.magnitude(this.velocity));

            this.location.add(this.velocity);

            this.velocity.add(this.acceleration);
            this.velocity.normalize().multiply(MicroMissleSpawnManager.SPEED);

            if (this.ticksLived % 10 == 0) {
                this.randomizeAcceleration();
            }
        }

        private void randomizeAcceleration() {
            Vector goDirection = targetLocation.toVector().subtract(location.toVector());
            goDirection.rotateAroundX(Math.toRadians(random.nextInt(MicroMissleSpawnManager.RANDOM_ACCELERATION_ANGLE * 2) - MicroMissleSpawnManager.RANDOM_ACCELERATION_ANGLE));
            goDirection.rotateAroundZ(Math.toRadians(random.nextInt(MicroMissleSpawnManager.RANDOM_ACCELERATION_ANGLE * 2) - MicroMissleSpawnManager.RANDOM_ACCELERATION_ANGLE));
            goDirection.rotateAroundY(Math.toRadians(random.nextInt(MicroMissleSpawnManager.RANDOM_ACCELERATION_ANGLE * 2) - MicroMissleSpawnManager.RANDOM_ACCELERATION_ANGLE));
            this.acceleration = goDirection.normalize().multiply(MicroMissleSpawnManager.ACCELERATION_SPEED);
        }

        private void die() {
            if (!this.isDead) {
                location.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, location, 0);
                location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                this.isDead = true;
            }
        }

        private static void particles(Location location) {
            location.getWorld().spawnParticle(Particle.FLAME, location, 1, 0.01, 0.01, 0.01, 0.014);
            location.getWorld().spawnParticle(Particle.SMOKE_NORMAL, location, 10, 0.04, 0.04, 0.04, 0.02);
        }
    }
}
