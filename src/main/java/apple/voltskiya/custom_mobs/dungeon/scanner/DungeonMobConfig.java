package apple.voltskiya.custom_mobs.dungeon.scanner;

import apple.voltskiya.custom_mobs.util.minecraft.InventoryUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * a configuration for a type of spawn
 * the material to represent
 */
public class DungeonMobConfig {
    public static final String PREFIX_TAG = "dungeon.config.";
    public String nameToRepresentMob;
    public List<DungeonMobInfo> mobs = new ArrayList<>();

    public DungeonMobConfig(String configName) {
        this.nameToRepresentMob = configName;
    }

    public DungeonMobConfig(JsonObject loadFrom) throws CommandSyntaxException {
        nameToRepresentMob = loadFrom.get(DungeonScanner.JsonKeys.MOB_CONFIG_NAME).getAsString();
        JsonArray mobsJson = loadFrom.get(DungeonScanner.JsonKeys.MOB_CONFIG_MOBS).getAsJsonArray();
        for (JsonElement element : mobsJson) {
            mobs.add(new DungeonMobInfo(element.getAsJsonObject()));
        }
    }

    public void add(DungeonMobInfo mob) {
        this.mobs.add(mob);
    }

    public String getName() {
        return nameToRepresentMob;
    }

    public List<DungeonMobInfo> getMobs() {
        return mobs;
    }

    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.add(DungeonScanner.JsonKeys.MOB_CONFIG_NAME, new JsonPrimitive(nameToRepresentMob));
        final JsonArray jsonMobs = new JsonArray();
        for (DungeonMobInfo mob : mobs) {
            jsonMobs.add(mob.toJson());
        }
        json.add(DungeonScanner.JsonKeys.MOB_CONFIG_MOBS, jsonMobs);

        return json;
    }

    public ItemStack toItem() {
        return InventoryUtils.makeItem(
                Material.ARMOR_STAND,
                1,
                this.getName(),
                Collections.emptyList());
    }
}
