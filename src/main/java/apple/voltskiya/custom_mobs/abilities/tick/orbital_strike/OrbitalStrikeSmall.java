package apple.voltskiya.custom_mobs.abilities.tick.orbital_strike;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.abilities.tick.orbital_strike.OrbitalStrikeConfig.OrbitalStrikeConfigSmall;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class OrbitalStrikeSmall {

    public static <Config extends OrbitalStrikeConfigSmall> void create(MMSpawned mob, Location location,
        Config config) {
        Random random = new Random();
        List<Integer> timings = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            timings.add(random.nextInt(config.targetTime / 5) + config.targetTime / 5 * i);
        }
        // make flames happen in a circle
        for (Integer xyzToTime : timings) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
                if (mob.isDead())
                    return;
                double radius = random.nextDouble() * config.targetVariationRadius;
                double theta = Math.toRadians(random.nextDouble() * 360);
                double xi = Math.cos(theta) * radius;
                double zi = Math.sin(theta) * radius;
                double yi = random.nextDouble() * (config.totalHeight - config.minHeight);
                Location center = location.clone().add(xi, yi + config.minHeight, zi);
                new OrbitalStrike<>(center, config);
            }, xyzToTime);
        }
    }

}
