package apple.voltskiya.custom_mobs.mobs.abilities.tick.lost_soul;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.mob_manager.listen.MMSpawnListener;
import apple.voltskiya.mob_manager.listen.SpawnHandlerListener;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Vex;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class BlemishSpawnManager implements SpawnHandlerListener, Listener {

    public static String SUMMON_VEX;
    private final HashSet<UUID> ghasts = new HashSet<>();

    public BlemishSpawnManager() {
        MMSpawnListener.get().addListener(this);
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
    }

    @Override
    public boolean shouldHandle(CreatureSpawnEvent event) {
        return event.getEntityType() == EntityType.GHAST;
    }

    @Override
    public void handle(MMSpawned mmSpawned) {
        if (mmSpawned.getEntity() instanceof Ghast ghast) {
            final UUID uuid = ghast.getUniqueId();
            ghasts.add(uuid);
        }
    }

    @Override
    public String getTag() {
        return "blemish_gateway";
    }

    @EventHandler(ignoreCancelled = true)
    public synchronized void shoot(ProjectileLaunchEvent event) {
        trim();
        if (event.getEntityType() == EntityType.FIREBALL) {
            @NotNull List<Entity> nearbyEntities = event.getEntity().getNearbyEntities(5, 5, 5);
            for (Entity nearby : nearbyEntities) {
                if (ghasts.contains(nearby.getUniqueId())) {
                    event.setCancelled(true);
                    shoot(nearby, event.getEntity().getLocation(), 2);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity().getScoreboardTags().contains("blemish_gateway")) {
            BlemishSpawnManager.shoot(event.getEntity(), event.getEntity().getLocation(), 5);
        }
    }

    public static void shoot(Entity me, Location location, int spawns) {
        Vector direction = location.getDirection();
        direction.multiply(0.3);

        Vector directionLeft = new Vector().copy(direction);
        final int angleClose = 22;
        directionLeft.setX(directionLeft.getX() * Math.cos(Math.toRadians(angleClose))
            - Math.sin(Math.toRadians(angleClose)) * directionLeft.getZ());
        directionLeft.setZ(directionLeft.getX() * Math.sin(Math.toRadians(angleClose))
            + Math.cos(Math.toRadians(angleClose)) * directionLeft.getZ());

        Vector directionRight = new Vector().copy(direction);
        directionRight.setX(directionRight.getX() * Math.cos(Math.toRadians(-angleClose))
            - Math.sin(Math.toRadians(-angleClose)) * directionRight.getZ());
        directionRight.setZ(directionRight.getX() * Math.sin(Math.toRadians(-angleClose))
            + Math.cos(Math.toRadians(-angleClose)) * directionRight.getZ());

        Vector directionLeftLeft = new Vector().copy(direction);
        final int angleFar = 45;
        directionLeftLeft.setX(directionLeftLeft.getX() * Math.cos(Math.toRadians(angleFar))
            - Math.sin(Math.toRadians(angleFar)) * directionLeftLeft.getZ());
        directionLeftLeft.setZ(directionLeftLeft.getX() * Math.sin(Math.toRadians(angleFar))
            + Math.cos(Math.toRadians(angleFar)) * directionLeftLeft.getZ());

        Vector directionRightRight = new Vector().copy(direction);
        directionRightRight.setX(directionRightRight.getX() * Math.cos(Math.toRadians(-angleFar))
            - Math.sin(Math.toRadians(-angleFar)) * directionRightRight.getZ());
        directionRightRight.setZ(directionRightRight.getX() * Math.sin(Math.toRadians(-angleFar))
            + Math.cos(Math.toRadians(-angleFar)) * directionRightRight.getZ());

        String cmd = "execute at " + me.getUniqueId() + " run summon vex " + location.getX() + " "
            + location.getY() + " " + location.getZ() + " " + SUMMON_VEX;
        for (int i = 0; i < spawns; i++) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
            @NotNull Collection<? extends Entity> vexes = location.getWorld()
                .getNearbyEntitiesByType(EntityType.VEX.getEntityClass(), location, 2);
            int i = 0;
            for (Entity entity : vexes) {
                Vector v = switch (i++) {
                    case 0 -> directionLeft;
                    case 1 -> directionRight;
                    case 2 -> direction;
                    case 3 -> directionLeftLeft;
                    case 4 -> directionRightRight;
                    default -> null;
                };
                if (v == null)
                    break;
                ((Vex) entity).setSummoner((Mob) me);
                entity.setVelocity(v);
                ((Vex) entity).setCharging(true);
            }
        }, 1);
    }

    private synchronized void trim() {
        ghasts.removeIf(u -> {
            Entity ghast = Bukkit.getEntity(u);
            return ghast == null || ghast.isDead();
        });
    }
}
