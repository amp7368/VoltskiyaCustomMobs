package apple.voltskiya.custom_mobs.dungeon.scanned;

import apple.voltskiya.custom_mobs.dungeon.PluginDungeon;
import apple.voltskiya.custom_mobs.dungeon.scanner.DungeonMobInfo;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.util.Vector;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public class DungeonLocation {
    private final Location dungeonLocation;

    public DungeonLocation(Location dungeonLocation) {
        this.dungeonLocation = dungeonLocation;
    }

    public DungeonLocation(JsonObject json) {
        this.dungeonLocation = new Location(
                Bukkit.getWorld(UUID.fromString(json.get("world").getAsString())),
                json.get("x").getAsDouble(),
                json.get("y").getAsDouble(),
                json.get("z").getAsDouble()
        ).setDirection(
                new Vector(
                        json.get("xF").getAsDouble(),
                        json.get("yF").getAsDouble(),
                        json.get("zF").getAsDouble()
                )
        );
    }

    public JsonElement toJson() {
        final JsonObject json = new JsonObject();
        json.add("world", new JsonPrimitive(this.dungeonLocation.getWorld().getUID().toString()));
        json.add("x", new JsonPrimitive(this.dungeonLocation.getX()));
        json.add("y", new JsonPrimitive(this.dungeonLocation.getY()));
        json.add("z", new JsonPrimitive(this.dungeonLocation.getZ()));
        json.add("xF", new JsonPrimitive(this.dungeonLocation.getDirection().getX()));
        json.add("yF", new JsonPrimitive(this.dungeonLocation.getDirection().getY()));
        json.add("zF", new JsonPrimitive(this.dungeonLocation.getDirection().getZ()));
        return json;
    }

    public Location getLocation() {
        return dungeonLocation;
    }

    public void spawn(DungeonMobInfo mob, Vector offset) {
        Optional<EntityTypes<?>> type = EntityTypes.a(mob.nbt);
        Location spawnLocation = dungeonLocation.clone().add(offset);
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
}
