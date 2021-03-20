package apple.voltskiya.custom_mobs.heartbeat.tick.orbital_strike;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.heartbeat.tick.main.UpdatedPlayerList;
import apple.voltskiya.custom_mobs.heartbeat.tick.orbital_strike.large.OrbitalStrikeManagerTicker;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrbitalStrike {
    private static final double MOVEMENT_SPEED = OrbitalStrikeManagerTicker.get().STRIKE_MOVEMENT_SPEED;
    private static final double MOVEMENT_LAG = OrbitalStrikeManagerTicker.get().STRIKE_MOVEMENT_LAG;
    private final Entity striker;
    private final LivingEntity target;
    private final Location strikerLocation;
    private final Location targetLocation;
    private final long callerUid;
    private double xt;
    private double yt;
    private double zt;
    private final World targetWorld;
    private final VoltskiyaPlugin plugin = VoltskiyaPlugin.get();
    private final Random random = new Random();
    private final List<Location> previousLocations = new ArrayList<>();
    private int currentTick = 0;

    public OrbitalStrike(Entity striker, LivingEntity target, long callerUid) {
        this.striker = striker;
        this.target = target;
        this.callerUid = callerUid;
        strikerLocation = striker.getLocation();
        targetLocation = target.getLocation();
        xt = targetLocation.getX();
        yt = targetLocation.getY();
        zt = targetLocation.getZ();
        targetWorld = targetLocation.getWorld();
        // make flames happen in a circle
        ((Mob) striker).setAI(false);
        ((Mob) striker).setTarget(null);
        strike();
        sound(strikerLocation);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            ((Mob) striker).setAI(true);
            ((Mob) striker).setTarget(target);
        }, OrbitalStrikeManagerTicker.get().STRIKE_TIME);
    }

    public synchronized void strike() {
        if (currentTick % 30 == 0)
            targetModify();
        if (currentTick % 5 == 0)
            target();
        if (currentTick >= OrbitalStrikeManagerTicker.get().STRIKE_TARGET_TIME)
            fireball();
        if (currentTick++ == OrbitalStrikeManagerTicker.get().STRIKE_TIME) {
            System.out.println("return");
            return;
        }
        System.out.println(currentTick);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::strike, 1);
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
                dx *= MOVEMENT_SPEED;
                dy *= MOVEMENT_SPEED;
                dz *= MOVEMENT_SPEED;
                xt += dx;
                yt += dy;
                zt += dz;
            }
        }
    }

    private synchronized void fireball() {
        double height = yt + 1;
        for (; height < yt + OrbitalStrikeManagerTicker.get().STRIKE_HEIGHT; height++) {
            if (!targetWorld.getBlockAt((int) xt, (int) height, (int) zt).getType().isAir()) {
                height--;
                break;
            }
        }
        final double finalHeight = height;
        for (int i = 0; i < 2; i++) {
            if (i == 1 && random.nextBoolean()) return;
            double theta = random.nextDouble() * 360;
            double radius = random.nextDouble() * OrbitalStrikeManagerTicker.get().STRIKE_TARGET_RADIUS;
            double x = Math.cos(Math.toRadians(theta)) * radius;
            double z = Math.sin(Math.toRadians(theta)) * radius;
            Location location = new Location(targetWorld, xt + x, finalHeight, zt + z);
            @NotNull Vector direction = new Vector(0, -1.5, 0);
            location.setDirection(direction);
            targetWorld.spawnEntity(location, i == 0 ? EntityType.SMALL_FIREBALL : EntityType.FIREBALL, CreatureSpawnEvent.SpawnReason.CUSTOM, fire -> {
                fire.setVelocity(direction);
            });
        }
    }


    private synchronized void target() {
        Location targetLocation = previousLocations.get((int) Math.max(0, previousLocations.size() - MOVEMENT_LAG));
        double xt = targetLocation.getX();
        double yt = targetLocation.getY();
        double zt = targetLocation.getZ();
        for (int i = 0; i < 100; i++) {
            double theta = random.nextDouble() * 360;
            double radius = random.nextDouble() * OrbitalStrikeManagerTicker.get().STRIKE_TARGET_RADIUS;
            double x = Math.cos(Math.toRadians(theta)) * radius;
            double z = Math.sin(Math.toRadians(theta)) * radius;
            double y = random.nextDouble() * .3;
            if (random.nextDouble() < 0.1) {
                targetWorld.spawnParticle(Particle.LAVA, xt + x, yt + y, zt + z, 1);
            }
            targetWorld.spawnParticle(Particle.REDSTONE, xt + x, yt + y, zt + z, 5, 0, 0, 0, 1,
                    new Particle.DustOptions(Color.fromBGR(0, 0, 99), 1.5f)
            );
        }

        // make flame outerCircle
        double radius = OrbitalStrikeManagerTicker.get().STRIKE_TARGET_RADIUS;
        for (int i = 0; i < 50; i++) {
            double theta = random.nextDouble() * 360;
            double radiusOffset = random.nextDouble() * .3;
            double x = Math.cos(Math.toRadians(theta)) * (radius + radiusOffset);
            double z = Math.sin(Math.toRadians(theta)) * (radius + radiusOffset);
            double y = random.nextDouble() * .3;
            targetWorld.spawnParticle(Particle.REDSTONE, xt + x, yt + y, zt + z, 5, 0, 0, 0, 1,
                    new Particle.DustOptions(Color.fromBGR(0, 0, 36), 1.5f)
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
            final int particles = 40;
            double xInterval = (x2 - x1) / particles;
            double zInterval = (z2 - z1) / particles;
            for (double i = 0, x = x1, z = z1; i < particles;
                 x += xInterval,
                         z += zInterval,
                         i++) {
                double y = random.nextDouble() * 0.3;
                targetWorld.spawnParticle(Particle.REDSTONE, xt + x, yt + y, zt + z, 5, 0, 0, 0, 1,
                        new Particle.DustOptions(Color.fromBGR(0, 0, 36), 1.5f)
                );
//                targetWorld.spawnParticle(Particle.FLAME, xt + x, yt + y, zt + z, 5, 0, 0, 0, 0);
            }
        }
    }

    private synchronized void sound(Location strikerLocation) {
        for (int time = 0; time < OrbitalStrikeManagerTicker.get().STRIKE_TIME; time += 10) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                strikerLocation.getWorld().playSound(strikerLocation, Sound.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 100f, .5f);
            }, time);
        }
    }
}
