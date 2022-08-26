package apple.voltskiya.custom_mobs.abilities.tick.orbital_strike;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.abilities.tick.orbital_strike.large.OrbitalStrikeConfig;
import apple.voltskiya.custom_mobs.abilities.tick.orbital_strike.large.OrbitalStrikeConfig.OrbitalStrikeTypeConfig;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;


public class OrbitalStrike {

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

    public OrbitalStrike(World targetWorld, double xt, double yt, double zt,
        OrbitalStrike.OrbitalStrikeType type) {
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
            strikerLocation.getWorld()
                .playSound(strikerLocation, Sound.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 30,
                    2f);
        } else if (type == OrbitalStrikeType.MEDIUM) {
            strikerLocation.getWorld()
                .playSound(strikerLocation, Sound.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 30,
                    1.2f);
        } else if (type == OrbitalStrikeType.LARGE) {
            strikerLocation.getWorld()
                .playSound(strikerLocation, Sound.BLOCK_END_PORTAL_SPAWN, SoundCategory.HOSTILE, 50,
                    1.5f);
        }
    }

    public synchronized void strike() {
        if (type.doesModify() && currentTick % 30 == 0) {
            targetModify();
        }
        if (currentTick % 5 == 0) {
            target();
        }
        if (currentTick >= type.getStrikeTargetTime()
            && currentTick % type.getFireballInterval() == 0)
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
            if (firebll == null)
                continue;
            targetWorld.spawnEntity(location, firebll, CreatureSpawnEvent.SpawnReason.CUSTOM,
                fire -> {
                    if (random.nextDouble() < .8)
                        ((Fireball) fire).setIsIncendiary(false);
                    fire.setVelocity(direction);
                });
        }
    }

    private void target() {
        Location targetLocation;
        if (previousLocations.isEmpty())
            targetLocation = new Location(targetWorld, xt, yt, zt);
        else
            targetLocation = previousLocations.get(
                (int) Math.max(0, previousLocations.size() - type.getMovementLag()));
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
                new Particle.DustOptions(Color.fromBGR(0, 0, 99), type.getParticleSize()));
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
                new Particle.DustOptions(Color.fromBGR(0, 0, 36), type.getParticleSize()));
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
                x += xInterval, z += zInterval, i++) {
                double y = random.nextDouble() * 0.3;
                targetWorld.spawnParticle(Particle.REDSTONE, xt + x, yt + y, zt + z, 5, 0, 0, 0, 1,
                    new Particle.DustOptions(Color.fromBGR(0, 0, 36), type.getParticleSize()));
//                targetWorld.spawnParticle(Particle.FLAME, xt + x, yt + y, zt + z, 5, 0, 0, 0, 0);
            }
        }
    }


    private void targetModify() {
        final Location oldLocation = new Location(targetWorld, xt, yt, zt);
        previousLocations.add(oldLocation);
        Player closest = UpdatedPlayerList.getClosestPlayerPlayer(oldLocation);
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
        SMALL(OrbitalStrikeConfig.get().small, (i) -> i == 0 ? EntityType.SMALL_FIREBALL : null),
        MEDIUM(OrbitalStrikeConfig.get().medium, (i) -> i == 0 ? EntityType.SMALL_FIREBALL : null),

        LARGE(OrbitalStrikeConfig.get().large,
            (i) -> i == 1 ? random.nextDouble() < .5 ? null : EntityType.FIREBALL
                : EntityType.SMALL_FIREBALL);
        private final Function<Integer, EntityType> fireball;
        private OrbitalStrikeTypeConfig config;

        OrbitalStrikeType(OrbitalStrikeTypeConfig config, Function<Integer, EntityType> fireball) {
            this.config = config;
            this.fireball = fireball;
        }

        public int getStrikeTargetTime() {
            return this.config.targetTime;
        }

        public double getMovementLag() {
            return this.config.movementTargetLag;
        }

        public double getMovementSpeed() {
            return this.config.movementSpeed;
        }

        public boolean doesModify() {
            return this == LARGE;
        }

        public int getTotalStrikeTime() {
            return this.config.totalTime;
        }

        public double getFireballMinHeight() {
            return this.config.minHeight;
        }

        public double getFireballHeight() {
            return this.config.height;
        }

        public double getTargetRadius() {
            return this.config.radius;
        }

        public EntityType getFireball(int i) {
            return fireball.apply(i);
        }

        public double getFireballInterval() {
            return this.config.shootInterval;
        }

        public int getParticles() {
            return 1;
        }

        public float getParticleSize() {
            return 0.7f;
        }

        public double getRange() {
            return config.targetingRange;
        }

        public long getCooldown() {
            return this.config.cooldown;
        }

        public double getChance() {
            return this.config.chancePerTickADouble;
        }
    }
}
