package apple.voltskiya.custom_mobs.dungeon.scanner;

import apple.voltskiya.custom_mobs.util.minecraft.InventoryUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.MojangsonParser;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.UUID;

/**
 * a single mob
 */
public class DungeonMobInfo {
    private final Entity entity;
    public NBTTagCompound nbt;
    public EntityTypes<?> mobType;
    private final String uuid;
    private final Vector location;

    public DungeonMobInfo(Entity entity) {
        nbt = new NBTTagCompound();
        ((CraftEntity) entity).getHandle().a_(nbt);
        nbt.remove("UUID");
        mobType = ((CraftEntity) entity).getHandle().getEntityType();
        this.entity = entity;
        this.uuid = entity.getUniqueId().toString();
        this.location = entity.getLocation().toVector();
    }

    public DungeonMobInfo(JsonObject loadFrom) throws CommandSyntaxException {
        this.nbt = MojangsonParser.parse(loadFrom.get(DungeonScanner.JsonKeys.MOB_CONFIG_NBT).getAsString());
        this.mobType = null;
        this.uuid = loadFrom.get(DungeonScanner.JsonKeys.MOB_CONFIG_UUID).getAsString();
        this.entity = Bukkit.getEntity(UUID.fromString(this.uuid));
        this.location = new Vector(
                loadFrom.get("x").getAsDouble(),
                loadFrom.get("y").getAsDouble(),
                loadFrom.get("z").getAsDouble()
        );
    }

    public Material getSpawnEgg() {
        return Material.BAT_SPAWN_EGG;
    }

    public String getName() {
        String name = nbt.getString("CustomName");
        return name.isEmpty() ? "No name" : name;
    }

    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.add(DungeonScanner.JsonKeys.MOB_CONFIG_NBT, new JsonPrimitive(nbt.asString()));
        json.add(DungeonScanner.JsonKeys.MOB_CONFIG_UUID, new JsonPrimitive(uuid));
        json.add("x", new JsonPrimitive(location.getX()));
        json.add("y", new JsonPrimitive(location.getY()));
        json.add("z", new JsonPrimitive(location.getZ()));
        return json;
    }

    public ItemStack toItem() {
        return InventoryUtils.makeItem(this.getSpawnEgg(), 1, this.getName(), null);
    }

    public Vector getLocation() {
        return location;
    }

    public ItemStack toItem(double distance) {
        return InventoryUtils.makeItem(this.getSpawnEgg(), 1, this.getName(),
                distance == Double.MAX_VALUE ? Collections.emptyList() :
                        Collections.singletonList(String.format("%.2f blocks away", distance)));
    }

    // todo
    public double getProbability() {
        return 1;
    }
}