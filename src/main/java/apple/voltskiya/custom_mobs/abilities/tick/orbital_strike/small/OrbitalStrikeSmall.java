package apple.voltskiya.custom_mobs.abilities.tick.orbital_strike.small;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.abilities.tick.orbital_strike.OrbitalStrike;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
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
    private final List<Integer> xyzsToTime = new ArrayList<>();
    private final int currentTick = 0;
    private final VoltskiyaPlugin plugin = VoltskiyaPlugin.get();
    private static final Random random = new Random();
    private final List<Location> previousLocations = new ArrayList<>();

    public OrbitalStrikeSmall(Entity striker, LivingEntity target, long callerUid) {
        this.callerUid = callerUid;
        Location targetLocation = target.getLocation();
        for (int i = 0; i < 5; i++) {
            xyzsToTime.add(
                    random.nextInt(SmallOrbitalStrikeManagerTicker.get().STRIKE_TIME / 5) + SmallOrbitalStrikeManagerTicker.get().STRIKE_TIME / 5 * i
            );
        }
        targetWorld = targetLocation.getWorld();
        // make flames happen in a circle
        for (Integer xyzToTime : xyzsToTime) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(),
                    () -> {
                        if (striker.isDead()) return;
                        final OrbitalStrike.OrbitalStrikeType type = random.nextDouble() < 1f / 3 ? OrbitalStrike.OrbitalStrikeType.MEDIUM : OrbitalStrike.OrbitalStrikeType.SMALL;
                        Location tLocation = target.getLocation();
                        Vector center = findRandomCenter(
                                tLocation.getX(),
                                tLocation.getY(),
                                tLocation.getZ()
                        );
                        new OrbitalStrike(
                                targetWorld,
                                center.getX(),
                                center.getY(),
                                center.getZ(),
                                type, callerUid
                        );
                    }, xyzToTime
            );
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
