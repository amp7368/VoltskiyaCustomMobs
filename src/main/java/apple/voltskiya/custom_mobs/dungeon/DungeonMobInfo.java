package apple.voltskiya.custom_mobs.dungeon;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.MojangsonParser;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;

public class DungeonMobInfo {
    public NBTTagCompound nbt;
    public EntityTypes<?> mobType;

    public DungeonMobInfo(Entity entity) {
        nbt = ((CraftEntity) entity).getHandle().save(new NBTTagCompound());
        mobType = ((CraftEntity) entity).getHandle().getEntityType();

    }

    public DungeonMobInfo(JsonObject loadFrom) throws CommandSyntaxException {
        this.nbt = MojangsonParser.parse(loadFrom.get(DungeonScanner.JsonKeys.MOB_CONFIG_NBT).getAsString());
        this.mobType = null;
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
        return json;
    }
}
