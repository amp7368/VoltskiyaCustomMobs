package apple.voltskiya.custom_mobs.trash.dungeon.product;


import apple.voltskiya.custom_mobs.trash.dungeon.PluginDungeon;
import apple.voltskiya.custom_mobs.trash.dungeon.gui.DungeonGui;
import apple.voltskiya.custom_mobs.trash.dungeon.scanned.DungeonLocation;
import apple.voltskiya.custom_mobs.trash.dungeon.scanned.DungeonScanned;
import apple.voltskiya.custom_mobs.trash.dungeon.scanner.DungeonScanner;
import apple.voltskiya.custom_mobs.trash.dungeon.scanner.JsonKeys;
import co.aikar.commands.BukkitCommandCompletionContext;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class Dungeon {
    private final String name;
    private final DungeonPlayerIO dungeonPlayerIO = new DungeonPlayerIO();
    private boolean wasLoaded;
    private DungeonLocation center = null;
    private DungeonScanner scanner = null;
    private DungeonScanned scanned = null;

    public Dungeon(String name) {
        this.name = name;
        fromJson(name);
    }

    private void fromJson(String name) {
        File file = getDungeonFile(name + ".json");
        try {
            if (!file.exists()) {
                this.wasLoaded = false;
            } else {
                this.wasLoaded = true;
                final FileReader reader = new FileReader(file);
                JsonObject json = new Gson().fromJson(reader, JsonObject.class);
                reader.close();
                JsonElement scannerJson = json.get(JsonKeys.Dungeon.SCANNER);
                if (scannerJson != null && !scannerJson.isJsonNull()) {
                    this.scanner = new DungeonScanner(this, scannerJson.getAsString());
                }
                JsonElement scannedJson = json.get(JsonKeys.Dungeon.SCANNED);
                if (scannedJson != null && !scannedJson.isJsonNull()) {
                    this.scanned = new DungeonScanned(this, scannedJson.getAsString());
                }
                JsonElement centerJson = json.get(JsonKeys.Dungeon.CENTER);
                if (centerJson != null && !centerJson.isJsonNull()) {
                    this.center = new DungeonLocation(this, centerJson.getAsJsonObject());
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            this.wasLoaded = false;
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

    public static Collection<String> getSchematics(BukkitCommandCompletionContext bukkitCommandCompletionContext) {
        final String[] files = getDungeonFolder().list((f, name) -> name.endsWith(".json"));
        if (files == null) return Collections.singleton("");
        for (int i = 0; i < files.length; i++) files[i] = files[i].substring(0, files[i].length() - 5);
        return Arrays.asList(files);
    }

    public DungeonScanned scanDungeon(ScanDungeonOptions scanDungeonOptions) throws IllegalStateException {
        if (this.scanner == null) {
            throw new IllegalStateException("No scanner in place");
        } else {
            this.scanned = scanner.scanDungeon(this.name, scanDungeonOptions);
            save();
            return this.scanned;
        }
    }

    private void save() {
        File file = getDungeonFile(name + ".json");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            JsonObject json = new JsonObject();
            if (scanner != null) {
                json.add(JsonKeys.Dungeon.SCANNER, new JsonPrimitive(scanner.getName()));
            }
            if (scanned != null) {
                json.add(JsonKeys.Dungeon.SCANNED, new JsonPrimitive(scanned.getName()));
            }
            if (center != null) {
                json.add(JsonKeys.Dungeon.CENTER, center.toJson());
            }
            final FileWriter writer = new FileWriter(file);
            new Gson().toJson(json, writer);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
            this.wasLoaded = false;
        }
    }

    public void scanMobConfig() {
        if (this.scanner == null) {
            scanner = new DungeonScanner(this, this.name);
        }
        scanner.scanMobConfig();
        save();
    }

    public boolean wasLoaded() {
        return wasLoaded;
    }

    /**
     * @param dungeonScannerName the name of the scanner to load
     * @return the scanner that was loaded
     */
    public DungeonScanner loadScanner(@Nullable String dungeonScannerName) {
        this.scanner = new DungeonScanner(this, dungeonScannerName == null ? name : dungeonScannerName);
        save();
        return this.scanner;
    }

    /**
     * @param dungeonInstanceName the name of the layout to load
     * @return the layout that was loaded
     */
    public DungeonScanned loadLayout(@Nullable String dungeonInstanceName) {
        this.scanned = new DungeonScanned(this, dungeonInstanceName);
        save();
        return this.scanned;
    }

    public String getName() {
        return name;
    }

    public DungeonScanner getScanner() {
        return scanner;
    }

    public DungeonLocation getDungeonLocation() {
        return center;
    }

    public void setDungeonLocation(Location location) {
        this.center = new DungeonLocation(this, location);
        save();
    }

    public DungeonScanned getScanned() {
        return scanned;
    }

    public DungeonPlayerIO getDungeonPlayerIO() {
        return dungeonPlayerIO;
    }

    public void gui(Player player) {
        player.openInventory(new DungeonGui(player, this).getInventory());
    }

    public void spawn(SpawnDungeonOptions spawnDungeonOptions) {
        if (this.scanner == null) throw new IllegalStateException("There no scanner in place");
        if (this.scanned == null) throw new IllegalStateException("There no layout in place");
        this.center.spawnAll(this.scanned, spawnDungeonOptions);
    }
}
