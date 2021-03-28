package apple.voltskiya.custom_mobs;

import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class Snowball implements Listener {
    private static final String SNOWBALL = "snowball";

    public Snowball() {
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
    }

    @EventHandler
    public void snowballEvent(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getItem();
            if (item != null && !item.getType().isAir()) {
                // we might have a snowball? O.o
                final ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta != null) {
                    String name = itemMeta.getDisplayName();
                    if (name.equals(SNOWBALL)) {
                        // we have a snowball!
                        snowball(event);
                    }
                }
            }
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
        private final Vector direction;
        private final World world;
        private final static Vector acceleration = new Vector(0, -0.02, 0);
        private final static Random random = new Random();
        private final static double radius = 5d;
        private int count = 0;

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
            if (!currentLocation.getBlock().getType().isAir()) return;
            if (player != null) {
                world.playSound(currentLocation, Sound.BLOCK_SNOW_BREAK, 40, .85f);
                player.damage(0);
                return;
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this, 1);
        }

        @Nullable
        private Player collision() {
            for (Player p : world.getPlayers()) {
                BoundingBox b = p.getBoundingBox();
                Vector[] corners = getSnowballCorners();
                for (Vector corner : corners) if (b.contains(corner)) return p;
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
                    new Vector(x - HITBOX * radius, y - HITBOX * radius, z - HITBOX * radius)
            };
        }

        private void snowballParticles() {
            BlockData blockData = Material.SNOW_BLOCK.createBlockData();
            for (int i = 0; i < 30; i++) {
                double theta = random.nextDouble() * 360;
                double thetay = random.nextDouble() * 360;
                double x = Math.cos(Math.toRadians(theta)) * radius;
                double y = Math.sin(Math.toRadians(theta)) * radius;
                double z = Math.sin(Math.toRadians(thetay)) * radius;
                world.spawnParticle(Particle.REDSTONE, currentLocation, 0, x, y, z, new Particle.DustOptions(Color.fromRGB(0xFFFFFF), 1.4f));
            }
        }
    }

    private static Vector[] getCorners(BoundingBox other) {
        Vector[] corners = new Vector[8];
        double xMin = other.getMinX();
        double yMin = other.getMinY();
        double zMin = other.getMinZ();
        double xMax = other.getMaxX();
        double yMax = other.getMaxY();
        double zMax = other.getMaxZ();

        int i = 0;
        for (double x = xMin; x <= xMax; x = xMax) {
            for (double y = yMin; y <= yMax; y = yMax) {
                for (double z = zMin; z <= zMax; z = zMax) {
                    corners[i++] = new Vector(x, y, z);
                    if (z == zMax) break;
                }
                if (y == yMax) break;
            }
            if (x == xMax) break;
        }
        return corners;
    }

}
