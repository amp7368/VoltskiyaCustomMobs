package apple.voltskiya.custom_mobs.dungeon.scanner;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.util.JsonUtils;
import apple.voltskiya.custom_mobs.util.minecraft.InventoryUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.entity.EntityTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.UUID;

/**
 * a single mob
 */
public class DungeonMobInfo {
    private final Entity entity;
    public NBTTagCompound nbt;
    public EntityTypes<?> mobType;
    private String uuid;
    private Location location;

    public DungeonMobInfo(Entity entity) {
        this.entity = entity;
        fromEntity(entity);
    }

    private void fromEntity(Entity entity) {
        nbt = new NBTTagCompound();
        DecodeEntity.do_d(((CraftEntity) entity).getHandle(), nbt);
        nbt.remove("UUID");
        mobType = ((CraftEntity) entity).getHandle().getEntityType();
        this.uuid = entity.getUniqueId().toString();
        this.location = entity.getLocation();
    }

    public DungeonMobInfo(JsonObject loadFrom) throws CommandSyntaxException {
        this.nbt = MojangsonParser.parse(loadFrom.get(JsonKeys.MOB_CONFIG_NBT).getAsString());
        this.mobType = null;
        this.uuid = loadFrom.get(JsonKeys.MOB_CONFIG_UUID).getAsString();
        this.entity = Bukkit.getEntity(UUID.fromString(this.uuid));
        this.location = JsonUtils.locationFromJson(loadFrom.get("location"));
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
        json.add(JsonKeys.MOB_CONFIG_NBT, new JsonPrimitive(nbt.asString()));
        json.add(JsonKeys.MOB_CONFIG_UUID, new JsonPrimitive(uuid));
        json.add("location", JsonUtils.locationToJson(location));
        return json;
    }

    public ItemStack toItem() {
        return InventoryUtils.makeItem(this.getSpawnEgg(), 1, this.getName(), null);
    }

    public Location getLocation() {
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

    public void rotate(int degrees) {
        if (entity != null) {
            final Location location = entity.getLocation();
            location.getDirection().rotateAroundY(Math.toRadians(degrees));
            entity.teleport(location);
            fromEntity(entity);
        }
    }

    public void pitchAdd(int degrees) {
        if (entity != null) {
            final Location location = entity.getLocation();
            location.setPitch(location.getPitch() + degrees);
            entity.teleport(location);
            fromEntity(entity);
        }
    }

    public String getUUID() {
        return uuid;
    }
}
