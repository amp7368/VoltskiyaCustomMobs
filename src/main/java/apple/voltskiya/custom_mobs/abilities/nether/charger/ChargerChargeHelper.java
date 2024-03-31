package apple.voltskiya.custom_mobs.abilities.nether.charger;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public class ChargerChargeHelper {

    private static final Random random = new Random();

    public static void stunParticles(Location eyeLocation, long upper) {
        for (int time = 0; time < upper; time += 3) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
                for (int i = 0; i < 20; i++) {
                    double xi = random.nextDouble() * 1.5 - .75;
                    double yi = random.nextDouble() * 1.5 - .75;
                    double zi = random.nextDouble() * 1.5 - .75;
                    eyeLocation.getWorld().spawnParticle(Particle.CRIT, eyeLocation, 0, xi, yi, zi);
                }
            }, time);
        }
    }

    public static void stunned(Location newLocation) {
        newLocation.getWorld().playSound(newLocation, Sound.ENTITY_HOGLIN_RETREAT, SoundCategory.HOSTILE, 100, 1.2f);
    }

    public static void chargeUpSound(Location location) {
        location.getWorld().playSound(location, Sound.ENTITY_ZOGLIN_ATTACK, SoundCategory.HOSTILE, 100, 0.5f);
    }

    public static void chargeSound(Location location) {
        location.getWorld().playSound(location, Sound.ENTITY_ZOGLIN_ANGRY, SoundCategory.HOSTILE, 200, 1.5f);
    }

    public static void runFeetParticles(Location location, Material type, int count) {
        World world = location.getWorld();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        @NotNull BlockData blockdata = type.createBlockData();
        for (int i = 0; i < count; i++) {
            double xi = random.nextDouble() * 2 - 1;
            double yi = random.nextDouble() * 2 - 1;
            double zi = random.nextDouble() * 2 - 1;

            world.spawnParticle(Particle.BLOCK_CRACK, x + xi, y + yi, z + zi, 1, blockdata);
        }
    }
}
