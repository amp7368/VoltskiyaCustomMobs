package apple.voltskiya.custom_mobs.heartbeat.tick.charger;

import apple.voltskiya.custom_mobs.DistanceUtils;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.heartbeat.tick.revive.ReviveDeadManager;
import apple.voltskiya.custom_mobs.heartbeat.tick.revive.ReviverIndividualTicker;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.PathEntity;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftMob;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class ChargerCharge {
    private static final Random random = new Random();

    public ChargerCharge(Mob charger, Location targetLocation) {
        Location chargerLocation = charger.getLocation();
        double x = targetLocation.getX() - chargerLocation.getX();
        double y = targetLocation.getY() - chargerLocation.getY();
        double z = targetLocation.getZ() - chargerLocation.getZ();
        x *= 3;
        y *= 3;
        z *= 3;
        Location finalLocation = chargerLocation.clone().add(x, y, z);
        PotionEffect strength = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20, 1);
        charger.addPotionEffect(strength);
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(),
                new MoveToTarget(charger, finalLocation, 0), 0); // get out of this stack trace
    }

    private static class MoveToTarget implements Runnable {
        private final Mob charger;
        private final Location finalLocation;
        private final int count;

        public MoveToTarget(Mob charger, Location finalLocation, int count) {
            this.charger = charger;
            this.finalLocation = finalLocation;
            this.count = count;
        }

        @Override
        public void run() {
            if (charger.isDead()) return;
            @NotNull Location finalLocationSameY = finalLocation.clone();
            finalLocationSameY.setY(charger.getLocation().getY());
            @Nullable Vector direction = DistanceUtils.unitVector(finalLocationSameY.subtract(charger.getLocation()).toVector());
            if (direction != null) {
                Vector looking = direction.clone().multiply(2); // look closer than a block away
                final @NotNull Location blockInFront = charger.getLocation().add(looking).add(0, 1, 0);
                if (blockInFront.getBlock().getType().isSolid()) {
                    // if the block directly in front of the mob is a block, stop charging
                    final Location chargerLocation = charger.getLocation();
                    final PathEntity path = ((CraftMob) charger).getHandle().getNavigation().a(
                            new BlockPosition(
                                    chargerLocation.getX(), chargerLocation.getY(), chargerLocation.getZ()
                            ), 1
                    );
                    ((CraftMob) charger).getHandle().getNavigation().a(path, 0f);
                    charger.setVelocity(new Vector(0, 0, 0));
                    particles(charger.getEyeLocation(), blockInFront.getBlock().getType());
                    ChargerManagerTicker.get().eatMob(charger);
                    charger.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                    return;
                }
            }
            if (DistanceUtils.distance(charger.getLocation(), finalLocation) > 2 && count < ChargerManagerTicker.MAX_CHARGE_TIME) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), new MoveToTarget(charger, finalLocation, count + 1), 1);
            } else {
                // we're done
                // add this back to the charge manager
                ChargerManagerTicker.get().eatMob(charger);
                charger.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
            }
            double x;
            double y;
            double z;
            if (direction != null) {
                Location chargerLocation = charger.getLocation();
                chargerLocation.add(direction.multiply(2.5));
                x = chargerLocation.getX();
                y = chargerLocation.getY();
                z = chargerLocation.getZ();
            } else {
                x = finalLocation.getX();
                y = finalLocation.getY();
                z = finalLocation.getZ();
            }
            ((CraftMob) charger).getHandle().getNavigation().a(x, y, z, 2f);

        }

        private static void particles(Location location, Material type) {
            World world = location.getWorld();
            double x = location.getX();
            double y = location.getY();
            double z = location.getZ();
            @NotNull BlockData blockdata = type.createBlockData();
            for (int i = 0; i < 50; i++) {
                double xi = random.nextDouble() * 2 - 1;
                double yi = random.nextDouble() * 2 - 1;
                double zi = random.nextDouble() * 2 - 1;
                world.spawnParticle(Particle.BLOCK_DUST, x + xi, y + yi, z + zi, 1, blockdata);
            }
            world.playSound(location, Sound.BLOCK_CHAIN_FALL, SoundCategory.HOSTILE, 100, .6f);
        }
    }
}
