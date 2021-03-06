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
        nameToRepresentMob = loadFrom.get(JsonKeys.MOB_CONFIG_NAME).getAsString();
        JsonArray mobsJson = loadFrom.get(JsonKeys.MOB_CONFIG_MOBS).getAsJsonArray();
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
        json.add(JsonKeys.MOB_CONFIG_NAME, new JsonPrimitive(nameToRepresentMob));
        final JsonArray jsonMobs = new JsonArray();
        for (DungeonMobInfo mob : mobs) {
            jsonMobs.add(mob.toJson());
        }
        json.add(JsonKeys.MOB_CONFIG_MOBS, jsonMobs);

        return json;
    }

    public ItemStack toItem() {
        return InventoryUtils.makeItem(
                Material.ARMOR_STAND,
                1,
                this.getName(),
                Collections.emptyList());
    }

    public ItemStack toItem(double distance) {
        return InventoryUtils.makeItem(
                Material.ARMOR_STAND,
                1,
                this.getName(),
                distance == Double.MAX_VALUE ? Collections.emptyList() :
                        Collections.singletonList(String.format("%.2f blocks away", distance)));
    }

    public DungeonMobInfo getSpawnedMob() {
        double totalProbability = 0;
        for (DungeonMobInfo mob : mobs) {
            totalProbability += mob.getProbability();
        }
        double choice = Math.random() * totalProbability;
        for (DungeonMobInfo mob : mobs) {
            choice -= mob.getProbability();
            if (choice <= 0) return mob;
        }
        return mobs.get(0);
    }
}
