package apple.voltskiya.custom_mobs.heartbeat.tick.orbital_strike;

import apple.voltskiya.custom_mobs.DistanceUtils;
import apple.voltskiya.custom_mobs.Pair;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.heartbeat.tick.main.UpdatedPlayerList;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

import static apple.voltskiya.custom_mobs.heartbeat.tick.orbital_strike.OrbitalStrikeManagerTicker.*;

public class OrbitalStrikeIndividualTicker {
    private final OrbitalStrikeManagerTicker.Closeness closeness;
    private boolean isCheckStrike = false;
    private final ArrayList<Pair<UUID, Long>> strikers = new ArrayList<>();
    private boolean isTicking = false;
    private long myTickerUid;
    private final Random random = new Random();
    private final Plugin plugin = VoltskiyaPlugin.get();

    public OrbitalStrikeIndividualTicker(OrbitalStrikeManagerTicker.Closeness closeness) {
        this.closeness = closeness;
    }

    public void tick() {
        Iterator<Pair<UUID, Long>> strikerUidIterator = strikers.iterator();
        boolean trim = false;
        long now = System.currentTimeMillis();
        while (strikerUidIterator.hasNext()) {
            Pair<UUID, Long> strikerUid = strikerUidIterator.next();
            if (now - strikerUid.getValue() < OrbitalStrikeManagerTicker.get().STRIKE_COOLDOWN) {
                continue;
            }
            Entity striker = Bukkit.getEntity(strikerUid.getKey());
            if (striker == null) {
                // remove this striker D:
                strikerUidIterator.remove();
                trim = true;
            } else {
                tickStriker(striker, strikerUid);
                if (OrbitalStrikeManagerTicker.get().amIGivingStriker(striker, closeness, strikerUid.getValue())) {
                    strikerUidIterator.remove();
                    trim = true;
                }
            }
        }
        if (trim) {
            strikers.trimToSize();
            if (isTicking && strikers.isEmpty()) {
                isTicking = false;
                closeness.getGiver().remove(myTickerUid);
            }
        }
    }

    private void tickStriker(Entity striker, Pair<UUID, Long> strikerUid) {
        if (isCheckStrike) {
            if (random.nextDouble() < OrbitalStrikeManagerTicker.get().STRIKE_CHANCE * closeness.getGiver().getTickSpeed()) {
                checkStrike(striker, strikerUid);
            }
        }
    }

    private void checkStrike(Entity striker, Pair<UUID, Long> strikerUid) {
        Location strikerLocation = striker.getLocation();
        @Nullable LivingEntity target = ((Mob) striker).getTarget();
        if (target == null) {
            Player closest = UpdatedPlayerList.getClosestPlayer(striker.getLocation());
            if (closest != null) {
                Location pLocation = closest.getLocation();
                double d = DistanceUtils.distance(pLocation, strikerLocation);
                if (d < OrbitalStrikeManagerTicker.get().STRIKE_DISTANCE && ((Mob) striker).hasLineOfSight(closest)) {
                    target = closest;
                }
            }
        }
        if (target != null) {
            // we have the target. time to orbital strike it
            strike(striker, target);
            strikerUid.setValue(System.currentTimeMillis());
        }
    }

    private void strike(Entity striker, LivingEntity target) {
        final Location strikerLocation = striker.getLocation();
        final Location targetLocation = target.getLocation();
        double xt = targetLocation.getX();
        double yt = targetLocation.getY();
        double zt = targetLocation.getZ();
        World targetWorld = targetLocation.getWorld();
        // make flames happen in a circle
        ((Mob) striker).setAI(false);
        ((Mob) striker).setTarget(null);
        sound(strikerLocation);
        target(xt, yt, zt, targetWorld);
        fireball(xt, yt, zt, targetWorld);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            ((Mob) striker).setAI(true);
            ((Mob) striker).setTarget(target);
        }, OrbitalStrikeManagerTicker.get().STRIKE_TIME);
    }

    private void fireball(double xt, double yt, double zt, World targetWorld) {
        double height = yt + 1;
        for (; height < yt +  OrbitalStrikeManagerTicker.get().STRIKE_HEIGHT; height++) {
            if (!targetWorld.getBlockAt((int) xt, (int) height, (int) zt).getType().isAir()) {
                height--;
                break;
            }
        }
        for (int time = OrbitalStrikeManagerTicker.get().STRIKE_TARGET_TIME; time < OrbitalStrikeManagerTicker.get().STRIKE_TIME; time += OrbitalStrikeManagerTicker.get().DESTRUCTION_BLAZE_INTERVAL) {
            final double finalHeight = height;
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
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
            }, time);
        }
    }


    private void target(double xt, double yt, double zt, World targetWorld) {
        for (int time = 0; time < OrbitalStrikeManagerTicker.get().STRIKE_TIME; time += 5) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                for (int i = 0; i < 100; i++) {
                    double theta = random.nextDouble() * 360;
                    double radius = random.nextDouble() * OrbitalStrikeManagerTicker.get().STRIKE_TARGET_RADIUS;
                    double x = Math.cos(Math.toRadians(theta)) * radius;
                    double z = Math.sin(Math.toRadians(theta)) * radius;
                    double y = random.nextDouble() * .3;
                    targetWorld.spawnParticle(Particle.FLAME, xt + x, yt + y, zt + z, 5, 0, 0, 0, 0);
                }
            }, time);
        }

//        // make flame tower happen
//        for (int time = 0; time < STRIKE_TIME; time += 5) {
//            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
//                for (int i = 0; i < 100; i++) {
//                    double theta = random.nextDouble() * 360;
//                    double radius = random.nextDouble() * .35;
//                    double height = random.nextDouble() * OrbitalStrikeManagerTicker.STRIKE_TARGET_TOWER_HEIGHT;
//                    double x = Math.cos(Math.toRadians(theta)) * radius;
//                    double z = Math.sin(Math.toRadians(theta)) * radius;
//                    targetWorld.spawnParticle(Particle.FLAME, xt + x, yt + height, zt + z, 5, 0, 0, 0, 0);
//                }
//            }, time);
//        }
        // make flame outerCircle
        for (int time = 0; time < OrbitalStrikeManagerTicker.get().STRIKE_TIME; time += 5) {
            final double radius = OrbitalStrikeManagerTicker.get().STRIKE_TARGET_RADIUS;
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                for (int i = 0; i < 50; i++) {
                    double theta = random.nextDouble() * 360;
                    double radiusOffset = random.nextDouble() * .3;
                    double x = Math.cos(Math.toRadians(theta)) * (radius + radiusOffset);
                    double z = Math.sin(Math.toRadians(theta)) * (radius + radiusOffset);
                    double y = random.nextDouble() * .3;
                    targetWorld.spawnParticle(Particle.FLAME, xt + x, yt + y, zt + z, 5, 0, 0, 0, 0);
                }
            }, time);
        }
        // make flame pentagram
        for (int time = 0; time < OrbitalStrikeManagerTicker.get().STRIKE_TIME; time += 5) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                final int angle = 360 / 5;
                double radius = OrbitalStrikeManagerTicker.get().STRIKE_TARGET_RADIUS * 1.5;
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
                        targetWorld.spawnParticle(Particle.FLAME, xt + x, yt + y, zt + z, 5, 0, 0, 0, 0);
                    }
                }
            }, time);
        }
    }

    private void sound(Location strikerLocation) {
        for (int time = 0; time < OrbitalStrikeManagerTicker.get().STRIKE_TIME; time += 10) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                strikerLocation.getWorld().playSound(strikerLocation, Sound.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 100f, .5f);
            }, time);
        }
    }

    public void giveStriker(Entity striker, long lastShot) {
        this.strikers.add(new Pair<>(striker.getUniqueId(), lastShot));
        if (!isTicking) {
            isTicking = true;
            this.myTickerUid = closeness.getGiver().add(this::tick);
        }
    }

    public void setIsCheckStrike() {
        this.isCheckStrike = true;
    }

}
