package apple.voltskiya.custom_mobs.abilities.tick.orbital_strike;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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


public class OrbitalStrike<Config extends OrbitalStrikeConfig> {

    private final Config config;
    private final Location location;
    private int currentTick = 0;
    private final VoltskiyaPlugin plugin = VoltskiyaPlugin.get();
    private static final Random random = new Random();
    private final List<Location> previousLocations = new ArrayList<>();

    public OrbitalStrike(Location targetLocation, Config config) {
        this.config = config;
        this.location = targetLocation;
        sound();
        strike();
    }

    public synchronized void sound() {
        if (config.getType() == OrbitalStrikeType.SMALL) {
            location.getWorld()
                .playSound(location, Sound.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 30, 2f);
        } else if (config.getType() == OrbitalStrikeType.MEDIUM) {
            location.getWorld()
                .playSound(location, Sound.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 30, 1.2f);
        } else if (config.getType() == OrbitalStrikeType.LARGE) {
            location.getWorld()
                .playSound(location, Sound.BLOCK_END_PORTAL_SPAWN, SoundCategory.HOSTILE, 50, 1.5f);
        }
    }

    public synchronized void strike() {
        if (config.doesModify && currentTick % 30 == 0) {
            targetModify();
        }
        if (currentTick % 5 == 0) {
            target();
        }
        if (currentTick >= config.targetTime && currentTick % config.shootInterval == 0)
            fireball();
        if (currentTick++ == config.totalTime) {
            return;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::strike, 1);
    }

    public void fireball() {
        Location higherLocation = location.clone().add(0, 1, 0);
        for (; higherLocation.getY() < location.getY() + config.totalHeight;
            higherLocation.add(0, 1, 0)) {
            if (!higherLocation.getBlock().getType().isAir()) {
                higherLocation.subtract(0, 1, 0);
                break;
            }
        }
        for (int i = 0; i < 2; i++) {
            double theta = random.nextDouble() * 360;
            double radius = random.nextDouble() * config.radius;
            double x = Math.cos(Math.toRadians(theta)) * radius;
            double z = Math.sin(Math.toRadians(theta)) * radius;
            higherLocation.add(x, 0, z);
            @NotNull Vector direction = new Vector(0, -1.5, 0);
            higherLocation.setDirection(direction);
            final EntityType firebll = config.fireballEntityType(i);
            if (firebll == null)
                continue;
            getWorld().spawnEntity(higherLocation, firebll, CreatureSpawnEvent.SpawnReason.SPELL,
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
            targetLocation = location.clone();
        else
            targetLocation = previousLocations.get(
                Math.max(0, previousLocations.size() - config.movementTargetLag));
        double xt = targetLocation.getX();
        double yt = targetLocation.getY();
        double zt = targetLocation.getZ();
        int particles = config.particles;
        for (int i = 0; i < particles; i++) {
            double theta = random.nextDouble() * 360;
            double radius = random.nextDouble() * config.radius;
            double x = Math.cos(Math.toRadians(theta)) * radius;
            double z = Math.sin(Math.toRadians(theta)) * radius;
            double y = random.nextDouble() * .3;
            if (config.getType() == OrbitalStrikeType.LARGE && random.nextDouble() < 0.1) {
                getWorld().spawnParticle(Particle.LAVA, xt + x, yt + y, zt + z, 1);
            }
            getWorld().spawnParticle(Particle.REDSTONE, xt + x, yt + y, zt + z, 5, 0, 0, 0, 1,
                new Particle.DustOptions(Color.fromBGR(0, 0, 99), config.particleSize));
        }

        // make flame outerCircle
        double radius = config.radius;
        for (int i = 0; i < particles / 2; i++) {
            double theta = random.nextDouble() * 360;
            double radiusOffset = random.nextDouble() * .3;
            double x = Math.cos(Math.toRadians(theta)) * (radius + radiusOffset);
            double z = Math.sin(Math.toRadians(theta)) * (radius + radiusOffset);
            double y = random.nextDouble() * .3;
            getWorld().spawnParticle(Particle.REDSTONE, xt + x, yt + y, zt + z, 5, 0, 0, 0, 1,
                new Particle.DustOptions(Color.fromBGR(0, 0, 36), config.particleSize));
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
                getWorld().spawnParticle(Particle.REDSTONE, xt + x, yt + y, zt + z, 5, 0, 0, 0, 1,
                    new Particle.DustOptions(Color.fromBGR(0, 0, 36), config.particleSize));
            }
        }
    }

    private World getWorld() {
        return this.location.getWorld();
    }


    private void targetModify() {
        final Location oldLocation = this.location.clone();
        previousLocations.add(oldLocation);
        Player closest = UpdatedPlayerList.getClosestPlayerPlayer(oldLocation);
        if (closest == null)
            return;
        final Location closestLocation = closest.getLocation();
        @NotNull Vector direction = oldLocation.subtract(closestLocation).toVector();
        if (direction.length() < .5)
            return;
        location.add(direction.normalize().multiply(config.movementSpeed));
    }


    public enum OrbitalStrikeType {
        SMALL,
        MEDIUM,
        LARGE
    }
}
