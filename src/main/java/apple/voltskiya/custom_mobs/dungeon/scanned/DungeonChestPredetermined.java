package apple.voltskiya.custom_mobs.dungeon.scanned;

import apple.voltskiya.custom_mobs.dungeon.scanner.DungeonScanner;
import apple.voltskiya.custom_mobs.util.minecraft.InventoryUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.v1_16_R3.MojangsonParser;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.TileEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DungeonChestPredetermined implements DungeonChest {
    private final NBTTagCompound nbt;
    private @Nullable
    final String name;
    private final NamespacedKey blockKey;
    private Location location = null;

    public DungeonChestPredetermined(NamespacedKey blockKey, TileEntity block, Location location, @Nullable String name) {
        this.blockKey = blockKey;
        this.nbt = block.b();
        this.location = location;
        this.name = name;
    }

    public DungeonChestPredetermined(JsonObject json) throws CommandSyntaxException {
        final String[] blockKey = json.get(DungeonScanner.JsonKeys.DUNGEON_CHESTS_BLOCK).getAsString().split(":");
        this.blockKey = new NamespacedKey(blockKey[0], blockKey[1]);
        this.nbt = MojangsonParser.parse(json.get(DungeonScanner.JsonKeys.DUNGEON_CHESTS_NBT).getAsString());
        final JsonElement nameJson = json.get(DungeonScanner.JsonKeys.DUNGEON_CHESTS_TITLE);
        this.name = nameJson == null || nameJson.isJsonNull() ? null : nameJson.getAsString();
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        JsonPrimitive inventoryJson = new JsonPrimitive(nbt.asString());
        json.add(DungeonScanner.JsonKeys.DUNGEON_CHESTS_BLOCK, new JsonPrimitive(blockKey.toString()));
        json.add(DungeonScanner.JsonKeys.DUNGEON_CHESTS_NBT, inventoryJson);
        json.add("typeId", new JsonPrimitive(ChestTypes.PREDETERMINED.getTypeName()));
        json.add(DungeonScanner.JsonKeys.DUNGEON_CHESTS_TITLE, name == null ? JsonNull.INSTANCE : new JsonPrimitive(name));
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
            lore.add("Predetermined chest");
            lore.add(String.format("%.2f blocks away", distance(player)));
            return InventoryUtils.makeItem(blockMaterial, 1, name, lore);
        }
        return InventoryUtils.makeItem(Material.CHEST, 1, "UNKNOWN", null);
    }
}
