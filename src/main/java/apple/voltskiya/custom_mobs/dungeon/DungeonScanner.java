package apple.voltskiya.custom_mobs.dungeon;

import apple.voltskiya.custom_mobs.dungeon.gui.DungeonGui;
import co.aikar.commands.BukkitCommandCompletionContext;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

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
    private final Map<Material, DungeonChestConfig> chestConfig = new HashMap<>();
    private Location pos1 = null;
    private Location pos2 = null;
    private final String name;
    private boolean wasLoaded;

    public DungeonScanner(String name) {
        this.name = name;
        try {
            load(name);
        } catch (IOException | CommandSyntaxException e) {
            e.printStackTrace();
        }
    }

    public void pos1(Location location) {
        this.pos1 = location;
    }

    public void pos2(Location location) {
        this.pos2 = location;
    }

    private void load(String name) throws IOException, CommandSyntaxException {
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

    private static File getScannerFile(String name) throws IOException {
        return new File(getScannerFolder(), name);
    }


    public void gui(Player player) {
        new DungeonGui(player, this);
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public List<DungeonMobConfig> getMobConfigs() {
        List<DungeonMobConfig> configs = new ArrayList<>(this.nameToMobConfig.values());
        configs.sort((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()));
        return configs;
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

    public void scan() {
        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() throws IOException {
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
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean wasLoaded() {
        return wasLoaded;
    }

    public static class JsonKeys {
        public static final String MOB_CONFIGS = "mobConfigs";
        public static final String MOB_CONFIG_NAME = "name";
        public static final String MOB_CONFIG_MOBS = "mobs";
        public static final String MOB_CONFIG_NBT = "nbt";
    }
}
