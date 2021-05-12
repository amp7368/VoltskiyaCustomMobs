package apple.voltskiya.custom_mobs.dungeon.scanner;

import apple.voltskiya.custom_mobs.dungeon.PluginDungeon;
import apple.voltskiya.custom_mobs.dungeon.gui.DungeonGui;
import apple.voltskiya.custom_mobs.dungeon.scanned.DungeonScanned;
import co.aikar.commands.BukkitCommandCompletionContext;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * represents a way to scan an area to turn it into a dungeon
 */
public class DungeonScanner {
    private final Map<String, DungeonMobConfig> nameToMobConfig = new HashMap<>();
    private Location pos1 = null;
    private Location pos2 = null;
    private final String name;
    private boolean wasLoaded;
    private DungeonScanned currentScannedDungeon = null;
    private Location center;

    public DungeonScanner(@Nullable Player player, String name) {
        this.name = name;
        this.center = player == null ? null : player.getLocation();
        try {
            fromJson(name);
        } catch (IOException | CommandSyntaxException e) {
            e.printStackTrace();
        }
    }

    public void gui(Player player) {
        new DungeonGui(player, this);
    }

    public void pos1(Location location) {
        this.pos1 = location;
    }

    public void pos2(Location location) {
        this.pos2 = location;
    }

    public void center(Location center) {
        this.center = center;
        if (this.currentScannedDungeon != null) this.currentScannedDungeon.center(center);
    }


    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    private void fromJson(String name) throws IOException, CommandSyntaxException {
        File scannerFile = getScannerFile(name + ".json");
        if (!scannerFile.exists()) {
            this.wasLoaded = false;
        } else {
            this.wasLoaded = true;
            JsonObject json = new Gson().fromJson(new FileReader(scannerFile), JsonObject.class);
            JsonArray mobConfigs = json.get(JsonKeys.MOB_CONFIGS).getAsJsonArray();
            for (JsonElement mobConfig : mobConfigs) {
                DungeonMobConfig realConfig = new DungeonMobConfig(mobConfig.getAsJsonObject());
                this.nameToMobConfig.put(realConfig.getName(), realConfig);
            }
        }
    }

    private static File getScannerFile(String name) {
        return new File(getScannerFolder(), name);
    }

    @NotNull
    private static File getScannerFolder() {
        File folder = new File(PluginDungeon.get().getDataFolder(), "scanners");
        folder.mkdirs();
        return folder;
    }

    public static Collection<String> getSchematics(BukkitCommandCompletionContext bukkitCommandCompletionContext) {
        final String[] files = getScannerFolder().list((f, name) -> name.endsWith(".json"));
        if (files == null) return Collections.singleton("");
        for (int i = 0; i < files.length; i++) files[i] = files[i].substring(0, files[i].length() - 5);
        return Arrays.asList(files);
    }

    public void scanDungeon(String dungeonInstanceName, Boolean scanBlocks, Boolean scanMobs, Boolean scanChests) {
        this.currentScannedDungeon = new DungeonScanned(this, dungeonInstanceName);
        if (scanBlocks == null && scanMobs == null && scanChests == null)
            this.currentScannedDungeon.scanAll();
        else if (scanBlocks != null && scanBlocks)
            this.currentScannedDungeon.scanBlocks(true);
        else if (scanMobs != null && scanMobs)
            this.currentScannedDungeon.scanMobs(true);
        else if (scanChests != null && scanChests)
            this.currentScannedDungeon.scanChests(true);
    }

    public DungeonScanned loadDungeonInstance(String dungeonInstanceName) {
        final DungeonScanned scannedDungeon = new DungeonScanned(this, dungeonInstanceName);
        if (scannedDungeon.isWasLoaded())
            this.currentScannedDungeon = scannedDungeon;
        return scannedDungeon;
    }

    public void scanMobConfig() {
        if (pos1 == null || pos2 == null) {
            throw new IllegalStateException("The positions have not been set yet");
        }
        World world = pos1.getWorld();
        Collection<Entity> entities = world.getNearbyEntities(new BoundingBox(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ()));
        String configName = null;
        Entity configEntity = null;
        for (Entity entity : entities) {
            for (String tag : entity.getScoreboardTags()) {
                if (tag.startsWith(DungeonMobConfig.PREFIX_TAG)) {
                    configName = tag.substring(DungeonMobConfig.PREFIX_TAG.length());
                    configEntity = entity;
                }
            }
        }
        if (configName == null) {
            throw new IllegalArgumentException("There is no entity with the tag " + DungeonMobConfig.PREFIX_TAG + " as a prefix");
        }
        DungeonMobConfig mobConfig = new DungeonMobConfig(configName);
        for (Entity entity : entities) {
            if (configEntity != entity && !(entity instanceof Player)) {
                // add the rest into the config
                mobConfig.add(new DungeonMobInfo(entity));
            }
        }
        nameToMobConfig.put(configName, mobConfig);
        try {
            toJson();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toJson() throws IOException {
        File scannerFile = getScannerFile(name + ".json");
        scannerFile.createNewFile();
        JsonObject json = new JsonObject();
        final JsonArray mobConfigs = new JsonArray();
        for (DungeonMobConfig mobConfig : nameToMobConfig.values())
            mobConfigs.add(mobConfig.toJson());
        json.add(JsonKeys.MOB_CONFIGS, mobConfigs);
        final FileWriter writer = new FileWriter(scannerFile);
        new Gson().toJson(json, writer);
        writer.close();
    }

    public boolean wasLoaded() {
        return wasLoaded;
    }

    public List<DungeonMobConfig> getMobConfigs() {
        List<DungeonMobConfig> configs = new ArrayList<>(this.nameToMobConfig.values());
        configs.sort((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()));
        return configs;
    }

    public @Nullable DungeonMobConfig getMobConfig(Entity e) {
        for (String tag : e.getScoreboardTags()) {
            if (tag.startsWith(DungeonMobConfig.PREFIX_TAG)) {
                String configName = tag.substring(DungeonMobConfig.PREFIX_TAG.length());
                DungeonMobConfig config = this.nameToMobConfig.get(configName);
                if (config != null) return config;
            }
        }
        return null;
    }

    public String name() {
        return name;
    }

    public @Nullable DungeonMobConfig getMobConfig(String name) {
        return this.nameToMobConfig.get(name);
    }

    @Nullable
    public DungeonScanned getDungeonInstance() {
        return currentScannedDungeon;
    }

    public void newDungeon(Location location) {
        this.currentScannedDungeon.newDungeon(location);
    }

    public Location getCenter() {
        return center;
    }

    public String getName() {
        return name;
    }


    public static class JsonKeys {
        public static final String MOB_CONFIGS = "mobConfigs";
        public static final String MOB_CONFIG_NAME = "name";
        public static final String MOB_CONFIG_MOBS = "mobs";
        public static final String MOB_CONFIG_NBT = "nbt";
        public static final String DUNGEON_MOBS = "mobs";
        public static final String DUNGEON_NAME = "dungeon_name";
        public static final String SCANNER_NAME = "scanner_name";
        public static final String DUNGEON_MOB_PRIMARY = "mobPrimary";
        public static final String DUNGEON_MOB_CONFIG = "mobConfig";
        public static final String MOB_CONFIG_UUID = "uuid";
        public static final String DUNGEON_CHESTS = "chests";
        public static final String DUNGEON_CHESTS_LOOTABLE = "lootable";
        public static final String DUNGEON_CHESTS_BLOCK = "blockId";
        public static final String DUNGEON_CHESTS_NBT = "nbt";
        public static final String DUNGEON_CHESTS_TITLE = "title";
        public static final String SCANNER_CENTER = "center";
        public static final String DUNGEON_REALS = "realDungeons";
        public static final String DUNGEON_REALS_NAME = "name";
    }
}
