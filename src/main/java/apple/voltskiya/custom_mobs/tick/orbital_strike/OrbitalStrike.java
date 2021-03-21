package apple.voltskiya.custom_mobs.tick.orbital_strike;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.main.UpdatedPlayerList;
import apple.voltskiya.custom_mobs.tick.orbital_strike.large.LargeOrbitalStrikeManagerTicker;
import apple.voltskiya.custom_mobs.tick.orbital_strike.small.SmallOrbitalStrikeManagerTicker;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;


public class OrbitalStrike {
    private final long callerUid;
    private final World targetWorld;
    private final OrbitalStrikeType type;
    private double xt;
    private double yt;
    private double zt;
    private int currentTick = 0;
    private final VoltskiyaPlugin plugin = VoltskiyaPlugin.get();
    private static final Random random = new Random();
    private final List<Location> previousLocations = new ArrayList<>();
    private final double targetRadius;

    public OrbitalStrike(World targetWorld, double xt, double yt, double zt, OrbitalStrike.OrbitalStrikeType type, long callerUid) {
        this.callerUid = callerUid;
        this.targetRadius = type.getTargetRadius();
        this.xt = xt;
        this.yt = yt;
        this.zt = zt;
        this.targetWorld = targetWorld;
        this.type = type;
        // make flames happen in a circle
        sound(new Location(targetWorld, xt, yt, zt));
        strike();
    }

    public synchronized void sound(Location strikerLocation) {
        if (type == OrbitalStrikeType.SMALL) {
            strikerLocation.getWorld().playSound(strikerLocation, Sound.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 30, 2f);
        } else if (type == OrbitalStrikeType.MEDIUM) {
            strikerLocation.getWorld().playSound(strikerLocation, Sound.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 30, 1.2f);
        } else if (type == OrbitalStrikeType.LARGE) {
            strikerLocation.getWorld().playSound(strikerLocation, Sound.BLOCK_END_PORTAL_SPAWN, SoundCategory.HOSTILE, 50, 1.5f);
        }
    }

    public synchronized void strike() {
        if (type.doesModify() && currentTick % 30 == 0) {
            targetModify();
        }
        if (currentTick % 5 == 0) {
            target();
        }
        if (currentTick >= type.getStrikeTargetTime() && currentTick % type.getFireballInterval() == 0)
            fireball();
        if (currentTick++ == type.getTotalStrikeTime()) {
            return;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::strike, 1);
    }

    public void fireball() {
        double height = yt + 1;
        for (; height < yt + type.getFireballHeight(); height++) {
            if (!targetWorld.getBlockAt((int) xt, (int) height, (int) zt).getType().isAir()) {
                height--;
                break;
            }
        }
        final double finalHeight = height;
        for (int i = 0; i < 2; i++) {
            double theta = random.nextDouble() * 360;
            double radius = random.nextDouble() * targetRadius;
            double x = Math.cos(Math.toRadians(theta)) * radius;
            double z = Math.sin(Math.toRadians(theta)) * radius;
            Location location = new Location(targetWorld, xt + x, finalHeight, zt + z);
            @NotNull Vector direction = new Vector(0, -1.5, 0);
            location.setDirection(direction);
            final EntityType firebll = type.getFireball(i);
            if (firebll == null) continue;
            targetWorld.spawnEntity(location,
                    firebll,
                    CreatureSpawnEvent.SpawnReason.CUSTOM,
                    fire -> {
                        if (random.nextDouble() < .8)
                            ((Fireball) fire).setIsIncendiary(false);
                        fire.setVelocity(direction);
                    }
            );
        }
    }

    private void target() {
        Location targetLocation;
        if (previousLocations.isEmpty())
            targetLocation = new Location(targetWorld, xt, yt, zt);
        else
            targetLocation = previousLocations.get((int) Math.max(0, previousLocations.size() - type.getMovementLag()));
        double xt = targetLocation.getX();
        double yt = targetLocation.getY();
        double zt = targetLocation.getZ();
        int particles = type.getParticles();
        for (int i = 0; i < particles; i++) {
            double theta = random.nextDouble() * 360;
            double radius = random.nextDouble() * targetRadius;
            double x = Math.cos(Math.toRadians(theta)) * radius;
            double z = Math.sin(Math.toRadians(theta)) * radius;
            double y = random.nextDouble() * .3;
            if (type == OrbitalStrikeType.LARGE && random.nextDouble() < 0.1) {
                targetWorld.spawnParticle(Particle.LAVA, xt + x, yt + y, zt + z, 1);
            }
            targetWorld.spawnParticle(Particle.REDSTONE, xt + x, yt + y, zt + z, 5, 0, 0, 0, 1,
                    new Particle.DustOptions(Color.fromBGR(0, 0, 99), type.getParticleSize())
            );
        }

        // make flame outerCircle
        double radius = targetRadius;
        for (int i = 0; i < particles / 2; i++) {
            double theta = random.nextDouble() * 360;
            double radiusOffset = random.nextDouble() * .3;
            double x = Math.cos(Math.toRadians(theta)) * (radius + radiusOffset);
            double z = Math.sin(Math.toRadians(theta)) * (radius + radiusOffset);
            double y = random.nextDouble() * .3;
            targetWorld.spawnParticle(Particle.REDSTONE, xt + x, yt + y, zt + z, 5, 0, 0, 0, 1,
                    new Particle.DustOptions(Color.fromBGR(0, 0, 36), type.getParticleSize())
            );
//            targetWorld.spawnParticle(Particle.FLAME, xt + x, yt + y, zt + z, 5, 0, 0, 0, 0);
        }
        // make flame pentagram
        radius *= 1.5;
        final int angle = 360 / 5;
        for (int theta = 0; theta <= 360; theta += angle) {
            // starting point is (x1,z1)
            double x1 = Math.cos(Math.toRadians(theta)) * (radius);
            double z1 = Math.sin(Math.toRadians(theta)) * (radius);
            // ending point is (x2,z2)
            double x2 = Math.cos(Math.toRadians((theta + angle * 2) % 360)) * (radius);
            double z2 = Math.sin(Math.toRadians((theta + angle * 2) % 360)) * (radius);
            // x2 is bigger
            final int particlesMine = particles * 4 / 10;
            double xInterval = (x2 - x1) / particlesMine;
            double zInterval = (z2 - z1) / particlesMine;
            for (double i = 0, x = x1, z = z1; i < particlesMine;
                 x += xInterval,
                         z += zInterval,
                         i++) {
                double y = random.nextDouble() * 0.3;
                targetWorld.spawnParticle(Particle.REDSTONE, xt + x, yt + y, zt + z, 5, 0, 0, 0, 1,
                        new Particle.DustOptions(Color.fromBGR(0, 0, 36), type.getParticleSize())
                );
//                targetWorld.spawnParticle(Particle.FLAME, xt + x, yt + y, zt + z, 5, 0, 0, 0, 0);
            }
        }
    }


    private void targetModify() {
        final Location oldLocation = new Location(targetWorld, xt, yt, zt);
        previousLocations.add(oldLocation);
        Player closest = UpdatedPlayerList.getClosestPlayer(oldLocation, callerUid);
        if (closest != null) {
            final Location closestLocation = closest.getLocation();
            double dx = closestLocation.getX() - xt;
            double dy = closestLocation.getY() - yt;
            double dz = closestLocation.getZ() - zt;
            double magnitude = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (magnitude >= .1) {
                dx /= magnitude;
                dy /= magnitude;
                dz /= magnitude;
                dx *= type.getMovementSpeed();
                dy *= type.getMovementSpeed();
                dz *= type.getMovementSpeed();
                xt += dx;
                yt += dy;
                zt += dz;
            }
        }
    }


    public enum OrbitalStrikeType {
        SMALL(
                SmallOrbitalStrikeManagerTicker.get().STRIKE_TARGET_TIME,
                0,
                0,
                SmallOrbitalStrikeManagerTicker.get().STRIKE_TIME,
                0,
                SmallOrbitalStrikeManagerTicker.get().STRIKE_TARGET_RADIUS,
                SmallOrbitalStrikeManagerTicker.get().DESTRUCTION_BLAZE_INTERVAL,
                40, 0.5f,
                (i) -> i == 0 ? EntityType.SMALL_FIREBALL : null
        ),
        LARGE(LargeOrbitalStrikeManagerTicker.get().STRIKE_TARGET_TIME,
                LargeOrbitalStrikeManagerTicker.get().STRIKE_MOVEMENT_SPEED,
                LargeOrbitalStrikeManagerTicker.get().STRIKE_MOVEMENT_LAG,
                LargeOrbitalStrikeManagerTicker.get().STRIKE_TIME,
                LargeOrbitalStrikeManagerTicker.get().STRIKE_HEIGHT,
                LargeOrbitalStrikeManagerTicker.get().STRIKE_TARGET_RADIUS,
                LargeOrbitalStrikeManagerTicker.get().DESTRUCTION_BLAZE_INTERVAL,
                100, 1.5f,
                (i) -> i == 1 ?
                        random.nextDouble() < .5 ? null : EntityType.FIREBALL :
                        EntityType.SMALL_FIREBALL
        ), MEDIUM(
                SmallOrbitalStrikeManagerTicker.get().STRIKE_TARGET_TIME,
                0,
                0,
                SmallOrbitalStrikeManagerTicker.get().STRIKE_TIME,
                0,
                SmallOrbitalStrikeManagerTicker.get().STRIKE_TARGET_RADIUS * 2,
                SmallOrbitalStrikeManagerTicker.get().DESTRUCTION_BLAZE_INTERVAL,
                60, .75f,
                (i) -> i == 0 ? EntityType.SMALL_FIREBALL : null
        );

        private final int strikeTargetTime;
        private final double strikeMovementSpeed;
        private final double strikeMovementLag;
        private final int strikeTime;
        private final double strikeHeight;
        private final double strikeTargetRadius;
        private final Function<Integer, EntityType> fireball;
        private final double fireballInterval;
        private final int particles;
        private final float particleSize;

        OrbitalStrikeType(int strikeTargetTime, double strikeMovementSpeed, double strikeMovementLag,
                          int strikeTime, double strikeHeight, double strikeTargetRadius,
                          double fireballInterval, int particles, float particleSize,
                          Function<Integer, EntityType> fireball
        ) {
            this.strikeTargetTime = strikeTargetTime;
            this.strikeMovementSpeed = strikeMovementSpeed;
            this.strikeMovementLag = strikeMovementLag;
            this.strikeTime = strikeTime;
            this.strikeHeight = strikeHeight;
            this.strikeTargetRadius = strikeTargetRadius;
            this.fireball = fireball;
            this.particles = particles;
            this.particleSize = particleSize;
            this.fireballInterval = fireballInterval;
        }

        public int getStrikeTargetTime() {
            return this.strikeTargetTime;
        }

        public double getMovementLag() {
            return this.strikeMovementLag;
        }

        public double getMovementSpeed() {
            return this.strikeMovementSpeed;
        }

        public boolean doesModify() {
            return this == LARGE;
        }

        public int getTotalStrikeTime() {
            return this.strikeTime;
        }

        public double getFireballHeight() {
            return this.strikeHeight;
        }

        public double getTargetRadius() {
            return strikeTargetRadius;
        }

        public EntityType getFireball(int i) {
            return fireball.apply(i);
        }

        public double getFireballInterval() {
            return fireballInterval;
        }

        public int getParticles() {
            return particles;
        }

        public float getParticleSize() {
            return particleSize;
        }
    }
}
