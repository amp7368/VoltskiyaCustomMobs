package apple.voltskiya.custom_mobs.dungeon.scanned;

import apple.voltskiya.custom_mobs.dungeon.PluginDungeon;
import apple.voltskiya.custom_mobs.dungeon.scanner.DungeonMobConfig;
import apple.voltskiya.custom_mobs.dungeon.scanner.DungeonScanner;
import co.aikar.commands.BukkitCommandCompletionContext;
import com.destroystokyo.paper.loottable.LootableInventory;
import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.TileEntity;
import org.bukkit.Location;
import org.bukkit.Nameable;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class DungeonScanned {
    private final List<DungeonChestScanned> chests = new ArrayList<>();
    private final String dungeonName;
    private DungeonScanner dungeonScanner;
    private final List<DungeonMobScanned> mobs = new ArrayList<>();
    private DungeonLocation realDungeon = null;
    private boolean wasLoaded = false;
    private Location center;

    public DungeonScanned(DungeonScanner dungeonScanner, String dungeonName) {
        this.dungeonScanner = dungeonScanner;
        this.dungeonName = dungeonName;
        this.center = dungeonScanner.getCenter();
        try {
            fromJson();
        } catch (IOException | CommandSyntaxException e) {
            e.printStackTrace();
        }
    }

    public void fromJson() throws IOException, CommandSyntaxException {
        File dungeonFile = getDungeonFile(dungeonName + ".json");
        if (dungeonFile.exists()) {
            this.wasLoaded = true;
            JsonObject json = new Gson().fromJson(new FileReader(dungeonFile), JsonObject.class);
            if (this.dungeonScanner == null)
                this.dungeonScanner = new DungeonScanner(null, json.get(DungeonScanner.JsonKeys.SCANNER_NAME).getAsString());
            JsonArray mobsJson = json.get(DungeonScanner.JsonKeys.DUNGEON_MOBS).getAsJsonArray();
            for (JsonElement mobJson : mobsJson) {
                this.mobs.add(new DungeonMobScanned(dungeonScanner, mobJson.getAsJsonObject()));
            }
            JsonArray chestsJson = json.get(DungeonScanner.JsonKeys.DUNGEON_CHESTS).getAsJsonArray();
            for (JsonElement chestJson : chestsJson) {
                this.chests.add(DungeonChestScanned.fromJson(chestJson.getAsJsonObject()));
            }
            JsonObject realDungeonJson = json.get(DungeonScanner.JsonKeys.DUNGEON_REALS).getAsJsonObject();
            this.realDungeon = realDungeonJson == null || realDungeonJson.isJsonNull() ? null : new DungeonLocation(realDungeonJson);

            final JsonObject centerJson = json.get(DungeonScanner.JsonKeys.SCANNER_CENTER).getAsJsonObject();
            this.center = new Location(
                    null,
                    centerJson.get("x").getAsDouble(),
                    centerJson.get("y").getAsDouble(),
                    centerJson.get("z").getAsDouble()
            ).setDirection(
                    new Vector(
                            centerJson.get("xF").getAsDouble(),
                            centerJson.get("yF").getAsDouble(),
                            centerJson.get("zF").getAsDouble()
                    )
            );
        }
    }

    private static File getDungeonFile(String name) {
        return new File(getDungeonFolder(), name);
    }

    @NotNull
    private static File getDungeonFolder() {
        File folder = new File(PluginDungeon.get().getDataFolder(), "dungeons");
        folder.mkdirs();
        return folder;
    }

    public DungeonScanned(String dungeonName) {
        this.dungeonScanner = null;
        this.dungeonName = dungeonName;
        try {
            fromJson();
        } catch (IOException | CommandSyntaxException e) {
            e.printStackTrace();
        }
    }

    public void spawnAll() {
        this.spawnBlocks();
        this.spawnChests();
        this.spawnMobs();
    }

    public void scanAll() {
        this.scanBlocks(false);
        this.scanMobs(false);
        this.scanChests(false);
        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void spawnBlocks() {

    }

    public void scanMobs(boolean shouldSave) {
        mobs.clear();
        Location pos1 = this.dungeonScanner.getPos1();
        Location pos2 = this.dungeonScanner.getPos2();
        World world = pos1.getWorld();
        Collection<Entity> entities = world.getNearbyEntities(new BoundingBox(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ()));
        for (Entity entity : entities) {
            if (!(entity instanceof Player)) {
                @Nullable DungeonMobConfig mobConfig = dungeonScanner.getMobConfig(entity);
                final DungeonMobScanned dungeonMobScanned = new DungeonMobScanned(entity);
                if (mobConfig != null) {
                    dungeonMobScanned.setConfig(mobConfig);
                }
                mobs.add(dungeonMobScanned);
            }
        }
        if (shouldSave) try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void scanBlocks(boolean shouldSave) {
        if (shouldSave) try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void spawnChests() {

    }

    public boolean isWasLoaded() {
        return wasLoaded;
    }

    public void spawnMobs() {
        for (DungeonMobScanned mob : this.mobs) {
            mob.spawn(realDungeon, this.center);
        }
    }

    public void scanChests(boolean shouldSave) {
        this.chests.clear();
        Location pos1 = this.dungeonScanner.getPos1();
        Location pos2 = this.dungeonScanner.getPos2();
        World world = pos1.getWorld();
        int minX = pos1.getBlockX();
        int minY = pos1.getBlockY();
        int minZ = pos1.getBlockZ();
        int maxX = pos2.getBlockX();
        int maxY = pos2.getBlockY();
        int maxZ = pos2.getBlockZ();
        if (minX > maxX) {
            int temp = maxX;
            maxX = minX;
            minX = temp;
        }
        if (minY > maxY) {
            int temp = maxY;
            maxY = minY;
            minY = temp;
        }
        if (minZ > maxZ) {
            int temp = maxZ;
            maxZ = minZ;
            minZ = temp;
        }
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    BlockState block = world.getBlockAt(x, y, z).getState();
                    String name = block instanceof Nameable ? ((Nameable) block).getCustomName() : null;
                    if (block instanceof LootableInventory && ((LootableInventory) block).hasLootTable()) {
                        LootTable loottable = ((Lootable) block).getLootTable();
                        if (loottable == null) {
                            // idk what happened...
                            continue;
                        }
                        NamespacedKey blockKey = block.getType().getKey();
                        NamespacedKey lootTableKey = loottable.getKey();
                        chests.add(new DungeonChestScannedLootTable(blockKey, lootTableKey, block.getLocation(), name));
                    } else if (block instanceof Container) {
                        NamespacedKey blockKey = block.getType().getKey();
                        @javax.annotation.Nullable TileEntity tileEntity = ((CraftWorld) block.getLocation().getWorld()).getHandle().getTileEntity(new BlockPosition(block.getX(), block.getY(), block.getZ()));
                        if (tileEntity != null)
                            chests.add(new DungeonChestScannedPredetermined(blockKey, tileEntity, block.getLocation(), name));
                    }
                }
            }
        }
        if (shouldSave) try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void newDungeon(Location newLocation) {
        this.realDungeon = new DungeonLocation(newLocation);
        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.add(DungeonScanner.JsonKeys.SCANNER_NAME, new JsonPrimitive(dungeonScanner.getName()));
        json.add(DungeonScanner.JsonKeys.DUNGEON_NAME, new JsonPrimitive(dungeonName));
        json.add(DungeonScanner.JsonKeys.SCANNER_NAME, new JsonPrimitive(dungeonScanner.name()));
        JsonArray jsonMobs = new JsonArray();
        for (DungeonMobScanned mob : mobs) jsonMobs.add(mob.toJson());
        json.add(DungeonScanner.JsonKeys.DUNGEON_MOBS, jsonMobs);
        JsonArray jsonChests = new JsonArray();
        for (DungeonChestScanned chest : chests) jsonChests.add(chest.toJson());
        json.add(DungeonScanner.JsonKeys.DUNGEON_CHESTS, jsonChests);
        json.add(DungeonScanner.JsonKeys.DUNGEON_REALS, realDungeon == null ? JsonNull.INSTANCE : realDungeon.toJson());
        final JsonObject centerJson = new JsonObject();
        centerJson.add("x", new JsonPrimitive(this.center.getX()));
        centerJson.add("y", new JsonPrimitive(this.center.getY()));
        centerJson.add("z", new JsonPrimitive(this.center.getZ()));
        centerJson.add("xF", new JsonPrimitive(this.center.getDirection().getX()));
        centerJson.add("yF", new JsonPrimitive(this.center.getDirection().getY()));
        centerJson.add("zF", new JsonPrimitive(this.center.getDirection().getZ()));
        json.add(DungeonScanner.JsonKeys.SCANNER_CENTER, centerJson);

        return json;
    }

    public void save() throws IOException {
        File file = getDungeonFile(dungeonName + ".json");
        file.createNewFile();
        final FileWriter writer = new FileWriter(file);
        new Gson().toJson(toJson(), writer);
        writer.close();
    }

    public static Collection<String> getSchematics(BukkitCommandCompletionContext bukkitCommandCompletionContext) {
        final String[] files = getDungeonFolder().list((f, name) -> name.endsWith(".json"));
        if (files == null) return Collections.singleton("");
        for (int i = 0; i < files.length; i++) files[i] = files[i].substring(0, files[i].length() - 5);
        return Arrays.asList(files);
    }

    public List<DungeonMobScanned> getMobs() {
        return mobs;
    }

    public List<DungeonChestScanned> getChests() {
        return chests;
    }

    public String getName() {
        return dungeonName;
    }

    public void center(Location center) {
        this.center = center;
        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
