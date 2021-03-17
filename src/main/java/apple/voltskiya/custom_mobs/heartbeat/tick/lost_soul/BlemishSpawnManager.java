package apple.voltskiya.custom_mobs.heartbeat.tick.lost_soul;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.heartbeat.tick.SpawnEater;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Vex;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class BlemishSpawnManager implements Listener, SpawnEater {
    public static final String summonVex = "{PersistenceRequired:1b,Health:2f,LifeTicks:10000,Tags:[\"lost_soul\",\"base.deathtime\"],CustomName:'{\"text\":\"Lost Soul\",\"color\":\"gray\"}',HandItems:[{id:'minecraft:air',Count:1b},{}],ArmorItems:[{},{},{},{id:\"minecraft:player_head\",Count:1b,tag:{SkullOwner:{Id:[I;1764524634,373116153,-1439012925,1162853076],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzU3YjhhY2QxNDViZDNkZmIxZGI5NGJkYmVkNDU4ZmUxODQ2YTljODg0ODIyY2EzY2U4MWE0Y2Y4MCJ9fX0=\"}]}}}}],ArmorDropChances:[0.085F,0.085F,0.085F,-327.670F],ActiveEffects:[{Id:14b,Amplifier:1b,Duration:100000,ShowParticles:0b}],Attributes:[{Name:generic.max_health,Base:2},{Name:generic.attack_damage,Base:0}]}";
    private HashSet<UUID> ghasts = new HashSet<>();

    public BlemishSpawnManager() {
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
    }

    @Override
    public void eatEvent(CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.GHAST) {
            ghasts.add(event.getEntity().getUniqueId());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void shoot(ProjectileLaunchEvent event) {
        trim();
        if (event.getEntityType() == EntityType.FIREBALL) {
            @NotNull List<Entity> nearbyEntities = event.getEntity().getNearbyEntities(5, 5, 5);
            for (Entity nearby : nearbyEntities) {
                if (ghasts.contains(nearby.getUniqueId())) {
                    System.out.println("isghast");
                    event.setCancelled(true);
                    Location location = event.getEntity().getLocation();
                    Vector direction = location.getDirection();
                    direction.multiply(0.5);

                    Vector directionLeft = new Vector().copy(direction);
                    directionLeft.setX(directionLeft.getX() * Math.cos(Math.toRadians(30)) - Math.sin(Math.toRadians(30)) * directionLeft.getZ());
                    directionLeft.setZ(directionLeft.getX() * Math.sin(Math.toRadians(30)) + Math.cos(Math.toRadians(30)) * directionLeft.getZ());

                    Vector directionRight = new Vector().copy(direction);
                    directionRight.setX(directionRight.getX() * Math.cos(Math.toRadians(-30)) - Math.sin(Math.toRadians(-30)) * directionRight.getZ());
                    directionRight.setZ(directionRight.getX() * Math.sin(Math.toRadians(-30)) + Math.cos(Math.toRadians(-30)) * directionRight.getZ());

                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon vex " + location.getX() + " " + location.getY() + " " + location.getZ() + " " + summonVex);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon vex " + location.getX() + " " + location.getY() + " " + location.getZ() + " " + summonVex);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon vex " + location.getX() + " " + location.getY() + " " + location.getZ() + " " + summonVex);

                    Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
                        @NotNull Collection<? extends Entity> vexes = location.getWorld().getNearbyEntitiesByType(EntityType.VEX.getEntityClass(), location, 2);
                        int i = 0;
                        for (Entity entity : vexes) {
                            Vector v = switch (i++) {
                                case 0 -> directionLeft;
                                case 1 -> direction;
                                case 2 -> directionRight;
                                default -> null;
                            };
                            if (v == null) break;
                            ((Vex) entity).setSummoner((Mob) nearby);
                            entity.setVelocity(v);
                            System.out.println("set");
                        }
                    },1);
                }
            }
        }
    }

    private void trim() {
        ghasts.removeIf(u -> {
            Entity g = Bukkit.getEntity(u);
            return g == null || g.isDead();
        });
    }
}
