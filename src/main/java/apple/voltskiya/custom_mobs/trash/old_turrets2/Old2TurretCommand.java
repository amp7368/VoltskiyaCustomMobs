package apple.voltskiya.custom_mobs.trash.old_turrets2;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.custom_model.CustomModelData;
import apple.voltskiya.custom_mobs.custom_model.CustomModelDataEntity;
import apple.voltskiya.custom_mobs.custom_model.CustomModelPlugin;
import apple.voltskiya.custom_mobs.trash.old_turrets2.mobs.Old2TurretMob;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.logging.Level;

@CommandAlias("turret")
public class Old2TurretCommand extends BaseCommand {
    private final File turretFile;

    public Old2TurretCommand() {
        turretFile = new File(Old2TurretPlugin.get().getDataFolder(), "turretModel.yml");
        if (!turretFile.exists()) {
            Old2TurretPlugin.get().log(Level.SEVERE, "The turret model was not there");
        }
        VoltskiyaPlugin.get().getCommandManager().getCommandCompletions().registerCompletion("turret_type", Old2TurretType::typeNames);
        VoltskiyaPlugin.get().getCommandManager().registerCommand(this);
    }

    @Default
    @CommandCompletion("@turret_type")
    public void turretFinite(Player player, String type) {
        final Old2TurretType turretType = Old2TurretType.getType(type);
        if (turretType == null) {
            player.sendMessage(ChatColor.RED + "There is no turret-type with that name");
            return;
        }
        createTurretEntities(player.getLocation(), turretType.builder(player));
        player.sendMessage(ChatColor.GREEN + "Turret Created");
    }


    private void createTurretEntities(Location location, Old2TurretMob turretMob) {
        turretMob.setCenter(location);
        World world = location.getWorld();
        CustomModelData model = CustomModelPlugin.get().loadSchematic(turretFile);
        if (model == null) {
            Old2TurretPlugin.get().log(Level.SEVERE, "The turret model was not there");
            return;
        }
        final AtomicInteger turretEntityCount = new AtomicInteger(model.entities.size());
        for (CustomModelDataEntity entity : model.entities) {
            double angle = Math.atan2(entity.facingZ, entity.facingX);
            double facingX = Math.cos(angle);
            double facingZ = Math.sin(angle);

            Object typeString = entity.otherData.get("type");
            if (typeString == null) {
                // this is a normal entity
                spawnEntity(location, world, entity, facingX, facingZ, turretEntityCount, turretMob::addEntity, turretMob);
            } else {
                TurretEntityTypes type;
                try {
                    type = TurretEntityTypes.valueOf(typeString.toString());
                } catch (IllegalArgumentException e) {
                    spawnEntity(location, world, entity, facingX, facingZ, turretEntityCount, turretMob::addEntity, turretMob);
                    e.printStackTrace();
                    continue;
                }
                switch (type) {
                    case BOW -> spawnEntity(location, world, entity, facingX, facingZ, turretEntityCount, turretMob::addBowEntity, turretMob);
                    case REFILLED -> spawnEntity(location, world, entity, facingX, facingZ, turretEntityCount, turretMob::addRefilledEntity, turretMob);
                    case DURABILITY -> spawnEntity(location, world, entity, facingX, facingZ, turretEntityCount, turretMob::addDurabilityEntity, turretMob);
                }
            }
        }

    }

    private void finishMob(Old2TurretMob turretMob) {
        Old2TurretList.registerOrUpdate(turretMob);
    }

    private void spawnEntity(Location location, World world, CustomModelDataEntity entity, double facingX, double facingZ, AtomicInteger turretEntityCount, Consumer<Entity> addEntityToMob, Old2TurretMob turretMob) {
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
            addEntityToMob.accept(spawned);
            if (turretEntityCount.decrementAndGet() == 0) {
                finishMob(turretMob);
            }
        });
    }

    private enum TurretEntityTypes {
        REFILLED,
        BOW,
        DURABILITY
    }

}
