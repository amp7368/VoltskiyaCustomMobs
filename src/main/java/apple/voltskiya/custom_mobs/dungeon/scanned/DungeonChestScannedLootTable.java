package apple.voltskiya.custom_mobs.dungeon.scanned;

import apple.voltskiya.custom_mobs.dungeon.scanner.JsonKeys;
import apple.voltskiya.custom_mobs.util.JsonUtils;
import apple.voltskiya.custom_mobs.util.minecraft.InventoryUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DungeonChestScannedLootTable implements DungeonChestScanned {
    private final NamespacedKey blockKey;
    private final NamespacedKey lootTableKey;
    private final @Nullable String name;
    private final Location location;

    public DungeonChestScannedLootTable(NamespacedKey blockKey, NamespacedKey lootTableKey, Location location, @Nullable String name) {
        this.blockKey = blockKey;
        this.lootTableKey = lootTableKey;
        this.location = location;
        this.name = name;
    }

    public DungeonChestScannedLootTable(JsonObject json) {
        final String[] lootTableKey = json.get(JsonKeys.DUNGEON_CHESTS_LOOTABLE).getAsString().split(":");
        this.lootTableKey = new NamespacedKey(lootTableKey[0], lootTableKey[1]);
        final String[] blockKey = json.get(JsonKeys.DUNGEON_CHESTS_BLOCK).getAsString().split(":");
        this.blockKey = new NamespacedKey(blockKey[0], blockKey[1]);
        final JsonElement nameJson = json.get(JsonKeys.DUNGEON_CHESTS_TITLE);
        this.name = nameJson == null || nameJson.isJsonNull() ? null : nameJson.getAsString();
        this.location = JsonUtils.locationFromJson(json.get("location"));
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.add(JsonKeys.DUNGEON_CHESTS_LOOTABLE, new JsonPrimitive(lootTableKey.toString()));
        json.add(JsonKeys.DUNGEON_CHESTS_BLOCK, new JsonPrimitive(blockKey.toString()));
        json.add("typeId", new JsonPrimitive(ChestTypes.LOOT_TABLE.getTypeName()));
        json.add(JsonKeys.DUNGEON_CHESTS_TITLE, name == null ? JsonNull.INSTANCE : new JsonPrimitive(name));
        json.add("location", JsonUtils.locationToJson(location));
        return json;
    }

    @Nullable
    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public ItemStack toItem(Player player) {
        Material blockMaterial = Material.matchMaterial(blockKey.toString());
        if (blockMaterial != null && blockMaterial.isItem()) {
            final List<String> lore = getLore();
            lore.add("LootTable: " + lootTableKey.toString());
            lore.add(String.format("%.2f blocks away", distance(player)));
            return InventoryUtils.makeItem(blockMaterial, 1, name, lore);
        }
        return InventoryUtils.makeItem(Material.CHEST, 1, "UNKNOWN", null);
    }
}
