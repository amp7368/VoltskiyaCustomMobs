package apple.voltskiya.custom_mobs.dungeon.scanned;

import apple.voltskiya.custom_mobs.dungeon.scanner.JsonKeys;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.utilities.util.JsonUtils;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

import java.util.List;

public class DungeonChestScannedPredetermined implements DungeonChestScanned {
    private final NBTTagCompound nbt;
    private final @Nullable String name;
    private final NamespacedKey blockKey;
    private final Location location;

    public DungeonChestScannedPredetermined(NamespacedKey blockKey, TileEntity block, Location location, @Nullable String name) {
        this.blockKey = blockKey;
        this.nbt = block.save(new NBTTagCompound());
        this.location = location;
        this.name = name;
    }

    public DungeonChestScannedPredetermined(JsonObject json) throws CommandSyntaxException {
        final String[] blockKey = json.get(JsonKeys.DUNGEON_CHESTS_BLOCK).getAsString().split(":");
        this.blockKey = new NamespacedKey(blockKey[0], blockKey[1]);
        this.nbt = MojangsonParser.parse(json.get(JsonKeys.DUNGEON_CHESTS_NBT).getAsString());
        final JsonElement nameJson = json.get(JsonKeys.DUNGEON_CHESTS_TITLE);
        this.name = nameJson == null || nameJson.isJsonNull() ? null : nameJson.getAsString();
        this.location = JsonUtils.locationFromJson(json.get("location"));
    }

    @Override
    public void setBlockAt(World world, Location spawnLocation) {
        Block blockAtLocation = world.getWorld().getBlockAt(spawnLocation);
        final Material blockType = Material.matchMaterial(blockKey.toString());
        if (blockType == null)
            throw new IllegalStateException(String.format("The block key at [%d, %d, %d] is not a material", location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        blockAtLocation.setType(blockType);
        world.setTileEntity(TileEntity.create(new BlockPosition(spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ()), null, nbt));
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        JsonPrimitive inventoryJson = new JsonPrimitive(nbt.asString());
        json.add(JsonKeys.DUNGEON_CHESTS_BLOCK, new JsonPrimitive(blockKey.toString()));
        json.add(JsonKeys.DUNGEON_CHESTS_NBT, inventoryJson);
        json.add("typeId", new JsonPrimitive(ChestTypes.PREDETERMINED.getTypeName()));
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
            lore.add("Predetermined chest");
            lore.add(String.format("%.2f blocks away", distance(player)));
            return InventoryUtils.makeItem(blockMaterial, 1, name, lore);
        }
        return InventoryUtils.makeItem(Material.CHEST, 1, "UNKNOWN", null);
    }


}
