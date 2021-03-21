package apple.voltskiya.custom_mobs.tick.lost_soul;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.tick.MobListSql;
import apple.voltskiya.custom_mobs.tick.SpawnEater;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class BlemishSpawnManager extends SpawnEater implements Listener {
    public final String SUMMON_VEX;
    private final HashSet<UUID> ghasts = new HashSet<>();

    public BlemishSpawnManager() throws IOException {
        SUMMON_VEX = (String) getValueOrInit(YmlSettings.SUMMON_VEX.getPath());
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
        for (UUID mob : getMobs()) {
            @Nullable Entity ghast = Bukkit.getEntity(mob);
            if (ghast == null) {
                MobListSql.removeMob(mob);
                continue;
            }
            ghasts.add(mob);
        }
    }

    @Override
    public synchronized void eatEvent(CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.GHAST) {
            final UUID uuid = event.getEntity().getUniqueId();
            ghasts.add(uuid);
            addMobs(uuid);
        }
    }

    @Override
    public synchronized String getName() {
        return "blemish";
    }

    @Override
    public synchronized void initializeYml() throws IOException {
        for (YmlSettings setting : YmlSettings.values()) {
            setValueIfNotExists(setting.getPath(), setting.getValue());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public synchronized void shoot(ProjectileLaunchEvent event) {
        trim();
        if (event.getEntityType() == EntityType.FIREBALL) {
            @NotNull List<Entity> nearbyEntities = event.getEntity().getNearbyEntities(5, 5, 5);
            for (Entity nearby : nearbyEntities) {
                if (ghasts.contains(nearby.getUniqueId())) {
                    event.setCancelled(true);
                    Location location = event.getEntity().getLocation();
                    Vector direction = location.getDirection();
                    direction.multiply(0.3);

                    Vector directionLeft = new Vector().copy(direction);
                    directionLeft.setX(directionLeft.getX() * Math.cos(Math.toRadians(30)) - Math.sin(Math.toRadians(30)) * directionLeft.getZ());
                    directionLeft.setZ(directionLeft.getX() * Math.sin(Math.toRadians(30)) + Math.cos(Math.toRadians(30)) * directionLeft.getZ());

                    Vector directionRight = new Vector().copy(direction);
                    directionRight.setX(directionRight.getX() * Math.cos(Math.toRadians(-30)) - Math.sin(Math.toRadians(-30)) * directionRight.getZ());
                    directionRight.setZ(directionRight.getX() * Math.sin(Math.toRadians(-30)) + Math.cos(Math.toRadians(-30)) * directionRight.getZ());


                    String cmd = "execute at " + nearby.getUniqueId().toString() + " run summon vex " + location.getX() + " " + location.getY() + " " + location.getZ() + " " + SUMMON_VEX;
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
                        @NotNull Collection<? extends Entity> vexes = location.getWorld().getNearbyEntitiesByType(EntityType.VEX.getEntityClass(), location, 2);
                        int i = 0;
                        for (Entity entity : vexes) {
                            Vector v;
                            switch (i++) {
                                case 0:
                                    v = directionLeft;
                                    break;
                                case 1:
                                    v = directionRight;
                                    break;
                                default:
                                    v = null;
                                    break;
                            }
                            if (v == null) break;
                            ((Vex) entity).setSummoner((Mob) nearby);
                            entity.setVelocity(v);
                            ((Vex) entity).setCharging(true);
                        }
                    }, 1);
                }
            }
        }
    }

    private synchronized void trim() {
        ghasts.removeIf(u -> {
            Entity g = Bukkit.getEntity(u);
            if (g == null || g.isDead()) {
                MobListSql.removeMob(u);
                return true;
            }
            return false;
        });
    }

    private enum YmlSettings {
        SUMMON_VEX("summonVexCommand", "{PersistenceRequired:1b,Health:2f,LifeTicks:10000,Tags:[\"lost_soul\",\"base.deathtime\"],CustomName:'{\"text\":\"Lost Soul\",\"color\":\"gray\"}',HandItems:[{id:'minecraft:air',Count:1b},{}],ArmorItems:[{},{},{},{id:\"minecraft:player_head\",Count:1b,tag:{SkullOwner:{Id:[I;1764524634,373116153,-1439012925,1162853076],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzU3YjhhY2QxNDViZDNkZmIxZGI5NGJkYmVkNDU4ZmUxODQ2YTljODg0ODIyY2EzY2U4MWE0Y2Y4MCJ9fX0=\"}]}}}}],ArmorDropChances:[0.085F,0.085F,0.085F,-327.670F],ActiveEffects:[{Id:14b,Amplifier:1b,Duration:100000,ShowParticles:0b}],Attributes:[{Name:generic.max_health,Base:2},{Name:generic.attack_damage,Base:0}]}");

        private final String path;
        private final Object value;

        YmlSettings(String path, Object value) {
            this.path = path;
            this.value = value;
        }

        public String getPath() {
            return path;
        }

        public Object getValue() {
            return value;
        }
    }
}
