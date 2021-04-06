package apple.voltskiya.custom_mobs.turrets;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.custom_model.CustomModel;
import apple.voltskiya.custom_mobs.custom_model.CustomModelPlugin;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

@CommandAlias("turret")
@CommandPermission("turret.create")
public class TurretCommand extends BaseCommand {
    private final File turretFile;

    public TurretCommand() {
        turretFile = new File(TurretPlugin.get().getDataFolder(), "turretModel.yml");
        if (!turretFile.exists()) {
            try {
                turretFile.createNewFile();
            } catch (IOException ignored) {
            }
        }
        VoltskiyaPlugin.get().getCommandManager().registerCommand(this);
    }

    @Subcommand("finite")
    public void turretFinite(Player player) {
        createTurretEntities(player.getLocation(), new TurretBuilder(player,TurretType.FINITE));
        player.sendMessage("Turret Created");
    }
    @Subcommand("infinite")
    public void turretInfinite(Player player) {
        createTurretEntities(player.getLocation(), new TurretBuilder(player,TurretType.INFINITE));
        player.sendMessage("Turret Created");
    }

    private final AtomicInteger turretCount = new AtomicInteger(0);

    private void createTurretEntities(Location location, TurretBuilder turretMob) {
        World world = location.getWorld();
        CustomModel model = CustomModelPlugin.get().loadSchematic(turretFile);
        if (model == null) return;
        for (CustomModel.CustomEntity entity : model.entities) {
            turretCount.incrementAndGet();

            Vector direction = location.getDirection();
//            double angle = Math.atan2(direction.getZ(), direction.getX()); TODO
            // TODO
            double angle = Math.atan2(entity.facingZ, entity.facingX);
            double facingX = Math.cos(angle);
            double facingZ = Math.sin(angle);

            Object typeString = entity.otherData.get("type");
            if (typeString == null) {
                // this is a normal entity
                world.spawnEntity(location.clone().add(entity.x, entity.y, entity.z).setDirection(
                        new Vector(
                                facingX,
                                entity.facingY,
                                facingZ
                        )
                ), entity.type, CreatureSpawnEvent.SpawnReason.CUSTOM, spawned -> {
                    Location loc = spawned.getLocation();
                    ((CraftEntity) spawned).getHandle().load(entity.nbt);
                    spawned.teleport(loc);
                    turretMob.addEntity(spawned);
                    turretCount.decrementAndGet();
                });
            } else {
                TurretEntityTypes type;
                try {
                    type = TurretEntityTypes.valueOf(typeString.toString());
                } catch (IllegalArgumentException e) {
                    world.spawnEntity(location.clone().add(entity.x, entity.y, entity.z).setDirection(
                            new Vector(
                                    facingX,
                                    entity.facingY,
                                    facingZ
                            )
                    ), entity.type, CreatureSpawnEvent.SpawnReason.CUSTOM, spawned -> {
                        Location loc = spawned.getLocation();
                        ((CraftEntity) spawned).getHandle().load(entity.nbt);
                        spawned.teleport(loc);
                        turretMob.addEntity(spawned);
                        turretCount.decrementAndGet();
                    });
                    e.printStackTrace();
                    continue;
                }
                switch (type) {
                    case BOW:
                        world.spawnEntity(location.clone().add(entity.x, entity.y, entity.z).setDirection(
                                new Vector(
                                        facingX,
                                        entity.facingY,
                                        facingZ
                                )
                        ), entity.type, CreatureSpawnEvent.SpawnReason.CUSTOM, spawned -> {
                            Location loc = spawned.getLocation();
                            ((CraftEntity) spawned).getHandle().load(entity.nbt);
                            spawned.teleport(loc);
                            turretMob.addBowEntity(spawned);
                            turretCount.decrementAndGet();
                        });
                        break;
                    case REFILLED:
                        world.spawnEntity(location.clone().add(entity.x, entity.y, entity.z).setDirection(
                                new Vector(
                                        facingX,
                                        entity.facingY,
                                        facingZ
                                )
                        ), entity.type, CreatureSpawnEvent.SpawnReason.CUSTOM, spawned -> {
                            Location loc = spawned.getLocation();
                            ((CraftEntity) spawned).getHandle().load(entity.nbt);
                            spawned.teleport(loc);
                            turretMob.addRefilledEntity(spawned);
                            turretCount.decrementAndGet();
                        });
                        break;
                    case DURABILITY:
                        world.spawnEntity(location.clone().add(entity.x, entity.y, entity.z).setDirection(
                                new Vector(
                                        facingX,
                                        entity.facingY,
                                        facingZ
                                )
                        ), entity.type, CreatureSpawnEvent.SpawnReason.CUSTOM, spawned -> {
                            Location loc = spawned.getLocation();
                            ((CraftEntity) spawned).getHandle().load(entity.nbt);
                            spawned.teleport(loc);
                            turretMob.addDurabilityEntity(spawned);
                            turretCount.decrementAndGet();
                        });
                }
            }
        }

        new Thread(() -> {
            while (turretCount.get() != 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // todo make this not busywait
                }
            }
            final TurretMob built;
            try {
                built = turretMob.build();
                Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), built::resetRotate);
                built.run();
                TurretManagerTicker.get().addTurret(built);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
    }

    private enum TurretEntityTypes {
        REFILLED,
        BOW,
        DURABILITY
    }
}
