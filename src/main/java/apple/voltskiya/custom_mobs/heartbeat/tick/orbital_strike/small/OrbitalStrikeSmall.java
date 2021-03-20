package apple.voltskiya.custom_mobs.heartbeat.tick.orbital_strike.small;

import apple.voltskiya.custom_mobs.Pair;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.heartbeat.tick.orbital_strike.large.OrbitalStrikeManagerTicker;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrbitalStrikeSmall {
    public static final double STRIKE_TARGET_LARGER_RADIUS = SmallOrbitalStrikeManagerTicker.get().STRIKE_TARGET_LARGER_RADIUS;
    public static final double STRIKE_HEIGHT = SmallOrbitalStrikeManagerTicker.get().STRIKE_HEIGHT;
    public static final double STRIKE_MIN_HEIGHT = SmallOrbitalStrikeManagerTicker.get().STRIKE_MIN_HEIGHT;
    private final long callerUid;
    private final World targetWorld;
    private List<Pair<Vector, Integer>> xyzsToTime;
    private int currentTick = 0;
    private final VoltskiyaPlugin plugin = VoltskiyaPlugin.get();
    private static final Random random = new Random();
    private final List<Location> previousLocations = new ArrayList<>();

    public OrbitalStrikeSmall(Entity striker, LivingEntity target, long callerUid) {
        this.callerUid = callerUid;
        Location strikerLocation = striker.getLocation();
        Location targetLocation = target.getLocation();
        xyzsToTime.add(
                new Pair<>(
                        findRandomCenter(
                                targetLocation.getX(),
                                targetLocation.getY(),
                                targetLocation.getZ()
                        ), 0
                )
        );
        xyzsToTime.add(
                new Pair<>(
                        findRandomCenter(
                                targetLocation.getX(),
                                targetLocation.getY(),
                                targetLocation.getZ()
                        ), 35
                )
        );
        xyzsToTime.add(
                new Pair<>(
                        findRandomCenter(
                                targetLocation.getX(),
                                targetLocation.getY(),
                                targetLocation.getZ()
                        ), 50
                )
        );
        targetWorld = targetLocation.getWorld();
        ((Mob) striker).setAI(false);
        ((Mob) striker).setTarget(null);
        // make flames happen in a circle
//        strike();
        sound(strikerLocation);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            ((Mob) striker).setAI(true);
            ((Mob) striker).setTarget(target);
        }, OrbitalStrikeManagerTicker.get().STRIKE_TIME);
    }
    private synchronized void sound(Location strikerLocation) {
        for (int time = 0; time < OrbitalStrikeManagerTicker.get().STRIKE_TIME; time += 10) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                strikerLocation.getWorld().playSound(strikerLocation, Sound.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.HOSTILE, 200f, .5f);
            }, time);
        }
    }
    private Vector findRandomCenter(double x, double y, double z) {
        double theta = random.nextDouble() * 360;
        double radius = random.nextDouble() * STRIKE_TARGET_LARGER_RADIUS;
        double xi = Math.cos(Math.toRadians(theta)) * radius;
        double zi = Math.sin(Math.toRadians(theta)) * radius;
        double yi = random.nextDouble() * (STRIKE_HEIGHT - STRIKE_MIN_HEIGHT) + STRIKE_MIN_HEIGHT;
        return new Vector(x + xi, y + yi, z + zi);
    }
}
