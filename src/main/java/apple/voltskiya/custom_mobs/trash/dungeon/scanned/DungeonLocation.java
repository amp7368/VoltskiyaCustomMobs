package apple.voltskiya.custom_mobs.trash.dungeon.scanned;

import apple.voltskiya.custom_mobs.trash.dungeon.PluginDungeon;
import apple.voltskiya.custom_mobs.trash.dungeon.product.Dungeon;
import apple.voltskiya.custom_mobs.trash.dungeon.product.SpawnDungeonOptions;
import apple.voltskiya.custom_mobs.trash.dungeon.scanner.DungeonMobInfo;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.util.Vector;
import voltskiya.apple.utilities.util.JsonUtils;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class DungeonLocation {
    private final Dungeon dungeon;
    private final Location dungeonLocation;

    public DungeonLocation(Dungeon dungeon, Location dungeonLocation) {
        this.dungeon = dungeon;
        this.dungeonLocation = dungeonLocation;
    }

    public DungeonLocation(Dungeon dungeon, JsonObject json) {
        this.dungeon = dungeon;
        this.dungeonLocation = JsonUtils.locationFromJson(json.get("location"));
    }

    public JsonElement toJson() {
        final JsonObject json = new JsonObject();
        json.add("location", JsonUtils.locationToJson(dungeonLocation));
        return json;
    }

    public Location getLocation() {
        return dungeonLocation;
    }

    private void spawnMobs(DungeonScanned layout, boolean spawnLayout) {
        List<DungeonMobScanned> mobs = layout.getMobs();
        for (DungeonMobScanned mob : mobs) {
            DungeonMobInfo spawnThis = spawnLayout ? mob.getMobPrimary() : mob.getMobSpawn();
            Vector offset = getOffset(this.dungeon.getScanned().getCenter(), mob.getLocation());
            spawn(spawnThis, offset);
        }
    }

    public void spawnAll(DungeonScanned layout, SpawnDungeonOptions spawnDungeonOptions) {

        if (spawnDungeonOptions.isSpawnMobs()) {
            spawnMobs(layout, spawnDungeonOptions.isSpawnLayout());
        }
        if (spawnDungeonOptions.isSpawnChests()) {
            spawnChests(layout, spawnDungeonOptions.isSpawnLayout());
        }

    }

    public void spawn(DungeonMobInfo mob, Vector offset) {
        Optional<EntityTypes<?>> type = EntityTypes.a(mob.nbt);
        Location spawnLocation = dungeonLocation.clone().add(offset);
        spawnLocation.setDirection(mob.getLocation().getDirection());
        World world = ((CraftWorld) spawnLocation.getWorld()).getHandle();
        if (type.isPresent()) {
            Entity entity = type.get().a(world);
            if (entity == null) {
                PluginDungeon.get().log(Level.WARNING, String.format("There was an error spawning %s at <%d, %d, %d>", mob.getName(), spawnLocation.getBlockX(), spawnLocation.getBlockY(), spawnLocation.getBlockZ()));
                return;
            }
            entity.load(mob.nbt);
            entity.setLocation(spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ(), spawnLocation.getYaw(), spawnLocation.getPitch());
            world.addEntity(entity);
        } else {
            PluginDungeon.get().log(Level.WARNING, String.format("%s is not a valid mob and did not spawn at <%d, %d, %d>", mob.getName(), spawnLocation.getBlockX(), spawnLocation.getBlockY(), spawnLocation.getBlockZ()));
        }
    }

    private void spawnChests(DungeonScanned layout, boolean spawnLayout) {
        List<DungeonChestScanned> chests = layout.getChests();
        for (DungeonChestScanned chest : chests) {
            Vector offset = getOffset(layout.getCenter(), chest.getLocation());
            spawn(chest, offset);
        }
    }

    public Vector getOffset(Location center, Location mobLocation) {
        if (mobLocation == null || center == null)
            throw new IllegalStateException("There is no location in the scanned mob or the center");
        return mobLocation.toVector().subtract(center.toVector());
    }

    public void spawn(DungeonChestScanned chest, Vector offset) {
        Location spawnLocation = dungeonLocation.clone().add(offset);
        World world = ((CraftWorld) spawnLocation.getWorld()).getHandle();
        chest.setBlockAt(world, spawnLocation);
    }
}
