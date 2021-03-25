package apple.voltskiya.custom_mobs.turrets;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

@CommandAlias("turret")
public class TurretCommand extends BaseCommand {
    public TurretCommand() {
        VoltskiyaPlugin.get().getCommandManager().registerCommand(this);
    }

    @Default
    public void turret(Player player) {
        createTurretEntities(player.getLocation(), new TurretBuilder(player));
    }

    private void createTurretEntities(Location location, TurretBuilder turretMob) {
        World world = location.getWorld();
        world.spawnEntity(location, EntityType.ARMOR_STAND, CreatureSpawnEvent.SpawnReason.CUSTOM, turretMob::addEntity);
        world.spawnEntity(location, EntityType.ARMOR_STAND, CreatureSpawnEvent.SpawnReason.CUSTOM, turretMob::addRefilledEntity);
        world.spawnEntity(location, EntityType.ARMOR_STAND, CreatureSpawnEvent.SpawnReason.CUSTOM, turretMob::addBowEntity);
        world.spawnEntity(location, EntityType.ARMOR_STAND, CreatureSpawnEvent.SpawnReason.CUSTOM, turretMob::addDurabilityEntity);
        new Thread(turretMob.build()).start();
    }
}
