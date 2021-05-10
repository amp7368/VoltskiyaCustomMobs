package apple.voltskiya.custom_mobs.dungeon.scanned;

import apple.voltskiya.custom_mobs.dungeon.PluginDungeon;
import apple.voltskiya.custom_mobs.dungeon.scanner.DungeonMobConfig;
import apple.voltskiya.custom_mobs.dungeon.scanner.DungeonScanner;
import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DungeonScanned {
    private final DungeonScanner dungeonScanner;
    private final String dungeonName;
    private final List<DungeonMobScanned> mobs = new ArrayList<>();
    private boolean wasLoaded = false;

    public DungeonScanned(DungeonScanner dungeonScanner, String dungeonName) {
        this.dungeonScanner = dungeonScanner;
        this.dungeonName = dungeonName;
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
            JsonArray mobsJson = json.get(DungeonScanner.JsonKeys.DUNGEON_MOBS).getAsJsonArray();
            for (JsonElement mobJson : mobsJson) {
                this.mobs.add(new DungeonMobScanned(dungeonScanner, mobJson.getAsJsonObject()));
            }
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

    public void scanChests(boolean shouldSave) {
        if (shouldSave) try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void scanMobs(boolean shouldSave) {
        mobs.clear();
        Location pos1 = this.dungeonScanner.getPos1();
        Location pos2 = this.dungeonScanner.getPos2();
        World world = pos1.getWorld();
        Collection<Entity> entities = world.getNearbyEntities(new BoundingBox(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ()));
        for (Entity entity : entities) {
            @Nullable DungeonMobConfig mobConfig = dungeonScanner.getMobConfig(entity);
            final DungeonMobScanned dungeonMobScanned = new DungeonMobScanned(entity);
            if (mobConfig != null) {
                dungeonMobScanned.setConfig(mobConfig);
            }
            mobs.add(dungeonMobScanned);
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

    public boolean isWasLoaded() {
        return wasLoaded;
    }

    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.add(DungeonScanner.JsonKeys.DUNGEON_NAME, new JsonPrimitive(dungeonName));
        json.add(DungeonScanner.JsonKeys.SCANNER_NAME, new JsonPrimitive(dungeonScanner.name()));
        JsonArray jsonMobs = new JsonArray();
        for (DungeonMobScanned mob : mobs) jsonMobs.add(mob.toJson());
        json.add(DungeonScanner.JsonKeys.DUNGEON_MOBS, jsonMobs);
        return json;
    }

    public void save() throws IOException {
        File file = getDungeonFile(dungeonName + ".json");
        file.createNewFile();
        final FileWriter writer = new FileWriter(file);
        new Gson().toJson(toJson(), writer);
        writer.close();
    }
}
