package apple.voltskiya.custom_mobs.dungeon.scanned;

import apple.voltskiya.custom_mobs.dungeon.scanner.DungeonMobConfig;
import apple.voltskiya.custom_mobs.dungeon.scanner.DungeonMobInfo;
import apple.voltskiya.custom_mobs.dungeon.scanner.DungeonScanner;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class DungeonMobScanned {
    private final DungeonMobInfo mob;
    private DungeonMobConfig config = null;

    public DungeonMobScanned(Entity entity) {
        this.mob = new DungeonMobInfo(entity);
    }

    public DungeonMobScanned(DungeonScanner dungeonScanner, JsonObject fromJson) throws CommandSyntaxException {
        this.mob = new DungeonMobInfo(fromJson.get(DungeonScanner.JsonKeys.DUNGEON_MOB_PRIMARY).getAsJsonObject());
        JsonElement json = fromJson.get(DungeonScanner.JsonKeys.DUNGEON_MOB_CONFIG);
        if (json != null && !json.isJsonNull()) this.config = dungeonScanner.getMobConfig(json.getAsString());
    }

    public void setConfig(DungeonMobConfig mobConfig) {
        this.config = mobConfig;
    }

    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.add(DungeonScanner.JsonKeys.DUNGEON_MOB_PRIMARY, mob.toJson());
        json.add(DungeonScanner.JsonKeys.DUNGEON_MOB_CONFIG, config == null ? JsonNull.INSTANCE : new JsonPrimitive(config.getName()));
        return json;
    }

    public ItemStack toItem() {
        if (config == null) return mob.toItem();
        return config.toItem();
    }
}
