package apple.voltskiya.custom_mobs;

import apple.lib.pmc.PluginModule;
import apple.mc.utilities.inventory.item.InventoryUtils;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Snowball extends PluginModule implements Listener {

    private static final String SNOWBALL = "snowball";

    @Override
    public void enable() {
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
    }

    @Override
    public String getName() {
        return "Snowball";
    }

    @EventHandler
    public void snowballEvent(PlayerInteractEvent event) {
        if (event.getAction().isRightClick()) {
            @NotNull String name = InventoryUtils.get().getDisplayName(event.getItem());
            if (name.equals(SNOWBALL))
                snowball(event);
        }
    }


    private void snowball(PlayerInteractEvent event) {
        Location playerLocation = event.getPlayer().getEyeLocation();
        Vector direction = playerLocation.getDirection();
        new SnowballThrow(playerLocation.add(direction), direction.multiply(.75));
    }

    private static class SnowballThrow implements Runnable {

        public static final double HITBOX = .007;
        private final Location currentLocation;
        private final World world;
        private final static Vector acceleration = new Vector(0, -0.02, 0);
        private final Vector direction;
        private final static Random random = new Random();
        private final static double radius = 5d;

        public SnowballThrow(Location currentLocation, Vector direction) {
            this.currentLocation = currentLocation;
            this.direction = direction;
            this.world = currentLocation.getWorld();
            world.playSound(currentLocation, Sound.ENTITY_SNOWBALL_THROW, .2f, 1f);
            run();
        }

        @Override
        public void run() {
            direction.add(acceleration);
            currentLocation.add(direction);
            snowballParticles();
            Player player = collision();
            if (!currentLocation.getBlock().getType().isAir())
                return;
            if (player != null) {
                world.playSound(currentLocation, Sound.BLOCK_SNOW_BREAK, 40, .85f);
                return;
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this, 1);
        }

        @Nullable
        private Player collision() {
            for (Player p : world.getPlayers()) {
                BoundingBox b = p.getBoundingBox();
                Vector[] corners = getSnowballCorners();
                for (Vector corner : corners)
                    if (b.contains(corner))
                        return p;
            }
            return null;
        }

        private Vector[] getSnowballCorners() {
            double x = currentLocation.getX();
            double y = currentLocation.getY();
            double z = currentLocation.getZ();
            return new Vector[]{
                new Vector(x + HITBOX * radius, y + HITBOX * radius, z + HITBOX * radius),
                new Vector(x + HITBOX * radius, y + HITBOX * radius, z - HITBOX * radius),
                new Vector(x + HITBOX * radius, y - HITBOX * radius, z + HITBOX * radius),
                new Vector(x + HITBOX * radius, y - HITBOX * radius, z - HITBOX * radius),
                new Vector(x - HITBOX * radius, y + HITBOX * radius, z + HITBOX * radius),
                new Vector(x - HITBOX * radius, y + HITBOX * radius, z - HITBOX * radius),
                new Vector(x - HITBOX * radius, y - HITBOX * radius, z + HITBOX * radius),
                new Vector(x - HITBOX * radius, y - HITBOX * radius, z - HITBOX * radius)};
        }

        private void snowballParticles() {
            for (int i = 0; i < 30; i++) {
                double theta = random.nextDouble() * 360;
                double thetay = random.nextDouble() * 360;
                double x = Math.cos(Math.toRadians(theta)) * radius;
                double y = Math.sin(Math.toRadians(theta)) * radius;
                double z = Math.sin(Math.toRadians(thetay)) * radius;
                world.spawnParticle(Particle.REDSTONE, currentLocation, 0, x, y, z,
                    new Particle.DustOptions(Color.fromRGB(0xFFFFFF), 1.4f));
            }
        }
    }

}
