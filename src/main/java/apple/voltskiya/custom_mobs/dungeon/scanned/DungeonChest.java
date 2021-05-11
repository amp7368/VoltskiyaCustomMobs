package apple.voltskiya.custom_mobs.dungeon.scanned;

import apple.voltskiya.custom_mobs.util.VectorUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface DungeonChest {
    static DungeonChest fromJson(JsonObject json) throws CommandSyntaxException {
        String type = json.get("typeId").getAsString();
        for (ChestTypes realType : ChestTypes.values()) {
            if (realType.typeName.equals(type))
                return realType.fromJson(json);
        }
        return null;
    }

    JsonElement toJson();

    default double distance(Player player) {
        return getLocation() == null ? Double.MAX_VALUE : (VectorUtils.magnitude(player.getLocation().toVector().subtract(getLocation().toVector())));
    }

    @Nullable
    Location getLocation();

    ItemStack toItem(Player player);

    default List<String> getLore() {
        final Location location = getLocation();
        if (location == null) return null;
        return new ArrayList<>() {{
            add(String.format("[ %d , %d , %d]", location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        }};
    }

    enum ChestTypes {
        LOOT_TABLE("lootTable", DungeonChestLootTable::new),
        PREDETERMINED("predetermined", DungeonChestPredetermined::new);

        private final String typeName;
        private final FunctionThrowing<JsonObject, DungeonChest> constructor;

        ChestTypes(String typeName, FunctionThrowing<JsonObject, DungeonChest> constructor) {
            this.constructor = constructor;
            this.typeName = typeName;
        }

        public String getTypeName() {
            return typeName;
        }

        public DungeonChest fromJson(JsonObject json) throws CommandSyntaxException {
            return constructor.apply(json);
        }

        @FunctionalInterface
        public interface FunctionThrowing<T, R> {
            R apply(T t) throws CommandSyntaxException;
        }
    }
}
