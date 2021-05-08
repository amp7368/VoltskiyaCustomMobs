package apple.voltskiya.custom_mobs.abilities.tick.charger;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.util.constants.TagConstants;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ChargerChargeHelper {
    private static final Random random = new Random();

    public ChargerChargeHelper(Mob charger, Location targetLocation) {
        Location chargerLocation = charger.getLocation();
        double x = targetLocation.getX() - chargerLocation.getX();
        double y = targetLocation.getY() - chargerLocation.getY();
        double z = targetLocation.getZ() - chargerLocation.getZ();
        x *= 3;
        y *= 3;
        z *= 3;
        Location finalLocation = chargerLocation.clone().add(x, y, z);
        charger.addScoreboardTag(TagConstants.isDoingAbility);
    }


//    private static class MoveToTarget implements Runnable {
//        private final Mob charger;
//        private final Location finalLocation;
//        private final Location finalLocationFar;
//        private int count;
//
//        public MoveToTarget(Mob charger, Location finalLocation, Location finalLocationFar, int count) {
//            this.charger = charger;
//            this.finalLocation = finalLocation;
//            this.finalLocationFar = finalLocationFar;
//            this.count = count;
//        }
//
//        @Override
//        public void run() {
//            if (charger.isDead()) return;
//            @NotNull Location finalLocationSameY = finalLocationFar.clone();
//            finalLocationSameY.setY(charger.getLocation().getY());
//            @Nullable Vector direction = DistanceUtils.unitVector(finalLocationSameY.subtract(charger.getLocation()).toVector());
//            if (direction != null) {
//                Vector looking = direction.clone().multiply(2); // look closer than a block away
//                final @NotNull Location blockInFront = charger.getLocation().add(looking).add(0, 1, 0);
//                if (blockInFront.getBlock().getType().isSolid()) {
//                    // if the block directly in front of the mob is a block, stop charging
//                    final Location chargerLocation = charger.getLocation();
//                    final PathEntity path = ((CraftMob) charger).getHandle().getNavigation().a(
//                            new BlockPosition(
//                                    chargerLocation.getX(), chargerLocation.getY(), chargerLocation.getZ()
//                            ), 1
//                    );
//                    ((CraftMob) charger).getHandle().getNavigation().a(path, 0f);
//                    charger.setVelocity(new Vector(0, 0, 0));
//                    runFeetParticles(charger.getEyeLocation(), blockInFront.getBlock().getType(), 50);
//                    ChargerManagerTicker.get().eatMob(charger);
//                    charger.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
//                    charger.setAI(false);
//                    Location newLocation = charger.getLocation();
//                    newLocation.setDirection(newLocation.getDirection().setY(-1));
//                    charger.teleport(newLocation);
//                    stunned(newLocation);
//                    Location eyeLocation = charger.getEyeLocation();
//                    stunParticles(eyeLocation, ChargerManagerTicker.CHARGE_STUN_TIME);
//                    Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
//                        charger.removeScoreboardTag(TagConstants.isDoingAbility);
//                        charger.setAI(true);
//                    }, ChargerManagerTicker.CHARGE_STUN_TIME);
//                    return;
//                }
//            }
//            if (DistanceUtils.distance(charger.getLocation(), finalLocation) > 2 && count < ChargerManagerTicker.MAX_CHARGE_TIME) {
//                count++;
//                Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this, 1);
//            } else {
//                // we're done
//                // add this back to the charge manager
//                charger.setAI(false);
//                Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> charger.setAI(true), ChargerManagerTicker.CHARGE_TIRED_TIME);
//                stunParticles(charger.getEyeLocation(), ChargerManagerTicker.CHARGE_TIRED_TIME);
//                ChargerManagerTicker.get().eatMob(charger);
//                charger.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
//                charger.removeScoreboardTag(TagConstants.isDoingAbility);
//            }
//            double x;
//            double y;
//            double z;
//            x = finalLocation.getX();
//            y = finalLocation.getY();
//            z = finalLocation.getZ();
//            ((CraftMob) charger).getHandle().getNavigation().a(x, y, z, 2f);
//
//        }
//
//
//    }

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
            world.spawnParticle(Particle.BLOCK_DUST, x + xi, y + yi, z + zi, 1, blockdata);
        }
    }
}
