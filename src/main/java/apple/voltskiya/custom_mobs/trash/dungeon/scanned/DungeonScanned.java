package apple.voltskiya.custom_mobs.trash.dungeon.scanned;

import apple.voltskiya.custom_mobs.trash.dungeon.PluginDungeon;
import apple.voltskiya.custom_mobs.trash.dungeon.patrols.Patrol;
import apple.voltskiya.custom_mobs.trash.dungeon.product.Dungeon;
import apple.voltskiya.custom_mobs.trash.dungeon.scanner.DungeonScanner;
import apple.voltskiya.custom_mobs.trash.dungeon.scanner.JsonKeys;
import co.aikar.commands.BukkitCommandCompletionContext;
import com.destroystokyo.paper.loottable.LootableInventory;
import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.entity.TileEntity;
import org.bukkit.Location;
import org.bukkit.Nameable;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
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
    private final String layoutName;
    private final List<DungeonChestScanned> chests = new ArrayList<>();
    private final List<DungeonMobScanned> mobs = new ArrayList<>();
    private final Map<String, Patrol> patrols = new HashMap<>();
    private final @NotNull Dungeon dungeon;
    private @Nullable Location center;
    private boolean wasLoaded = false;

    public DungeonScanned(@NotNull Dungeon dungeon, String layoutName) {
        this.dungeon = dungeon;
        this.layoutName = layoutName;
        try {
            fromJson();
        } catch (IOException | CommandSyntaxException e) {
            e.printStackTrace();
        }
    }

    public void fromJson() throws IOException, CommandSyntaxException, IllegalStateException {
        File dungeonFile = getDungeonFile(layoutName + ".json");
        if (dungeonFile.exists()) {
            this.wasLoaded = true;
            JsonObject json = new Gson().fromJson(new FileReader(dungeonFile), JsonObject.class);
            JsonArray mobsJson = json.get(JsonKeys.Layout.DUNGEON_MOBS).getAsJsonArray();
            for (JsonElement mobJson : mobsJson) {
                this.mobs.add(new DungeonMobScanned(this.dungeon, mobJson.getAsJsonObject()));
            }
            JsonArray chestsJson = json.get(JsonKeys.Layout.DUNGEON_CHESTS).getAsJsonArray();
            for (JsonElement chestJson : chestsJson) {
                this.chests.add(DungeonChestScanned.fromJson(chestJson.getAsJsonObject()));
            }
            final JsonElement centerJsonNullable = json.get(JsonKeys.Layout.CENTER);
            if (centerJsonNullable == null || centerJsonNullable.isJsonNull()) {
                this.center = null;
            } else {
                final JsonObject centerJson = centerJsonNullable.getAsJsonObject();
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
    }

    private static File getDungeonFile(String name) {
        return new File(getDungeonFolder(), name);
    }

    @NotNull
    private static File getDungeonFolder() {
        File folder = new File(PluginDungeon.get().getDataFolder(), "layouts");
        folder.mkdirs();
        return folder;
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

    public void scanMobs(boolean shouldSave) {
        mobs.clear();
        DungeonScanner dungeonScanner = this.dungeon.getScanner();
        if (dungeonScanner == null) throw new IllegalStateException("No dungeon scanner is in place");
        Location pos1 = dungeon.getDungeonPlayerIO().getPos1();
        Location pos2 = dungeon.getDungeonPlayerIO().getPos2();
        if (pos1 == null || pos2 == null) throw new IllegalStateException("Pos1 or Pos2 are not set");
        World world = pos1.getWorld();
        Collection<Entity> entities = world.getNearbyEntities(new BoundingBox(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ()));
        for (Entity entity : entities) {
            if (!(entity instanceof Player)) {
                mobs.add(new DungeonMobScanned(dungeonScanner, entity));
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

    public void save() throws IOException {
        File file = getDungeonFile(layoutName + ".json");
        file.createNewFile();
        final FileWriter writer = new FileWriter(file);
        new Gson().toJson(toJson(), writer);
        writer.close();
    }

    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.add(JsonKeys.Layout.DUNGEON_NAME, new JsonPrimitive(layoutName));
        JsonArray jsonMobs = new JsonArray();
        for (DungeonMobScanned mob : mobs) jsonMobs.add(mob.toJson());
        json.add(JsonKeys.Layout.DUNGEON_MOBS, jsonMobs);
        JsonArray jsonChests = new JsonArray();
        for (DungeonChestScanned chest : chests) jsonChests.add(chest.toJson());
        json.add(JsonKeys.Layout.DUNGEON_CHESTS, jsonChests);
        if (center == null) {
            json.add(JsonKeys.Layout.CENTER, JsonNull.INSTANCE);
        } else {
            final JsonObject centerJson = new JsonObject();
            centerJson.add("x", new JsonPrimitive(this.center.getX()));
            centerJson.add("y", new JsonPrimitive(this.center.getY()));
            centerJson.add("z", new JsonPrimitive(this.center.getZ()));
            centerJson.add("xF", new JsonPrimitive(this.center.getDirection().getX()));
            centerJson.add("yF", new JsonPrimitive(this.center.getDirection().getY()));
            centerJson.add("zF", new JsonPrimitive(this.center.getDirection().getZ()));
            json.add(JsonKeys.Layout.CENTER, centerJson);
        }
        return json;
    }

    public void scanChests(boolean shouldSave) {
        this.chests.clear();
        DungeonScanner dungeonScanner = this.dungeon.getScanner();
        if (dungeonScanner == null) throw new IllegalStateException("No dungeon scanner is in place");
        Location pos1 = dungeon.getDungeonPlayerIO().getPos1();
        Location pos2 = dungeon.getDungeonPlayerIO().getPos2();
        if (pos1 == null || pos2 == null) throw new IllegalStateException("Pos1 or Pos2 are not set");
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
                        WorldServer worldHandle = ((CraftWorld) block.getLocation().getWorld()).getHandle();
                        @Nullable TileEntity tileEntity = worldHandle.getBlockEntity(new BlockPosition(block.getX(), block.getY(), block.getZ()), true);
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
        return layoutName;
    }

    public boolean wasLoaded() {
        return wasLoaded;
    }

    public @Nullable Location getCenter() {
        return center;
    }

    public void setCenter(Location center) {
        this.center = center;
        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public Patrol getPatrol(String patrolName) {
        return patrols.get(patrolName);
    }

    public void addPatrol(Patrol patrol) {
        this.patrols.put(patrol.getName(), patrol);
    }
}
