package apple.voltskiya.custom_mobs.heartbeat.tick;

import apple.voltskiya.custom_mobs.heartbeat.MobTickPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public abstract class SpawnEater {
    private static final String defaultConfig = "config";
    private File folder = null;
    private final Map<String, YamlConfiguration> ymls = new HashMap<>();

    abstract public void eatEvent(CreatureSpawnEvent event);

    abstract public String getName();

    abstract public void initializeYml() throws IOException;

    public void setValueIfNotExists(String path, Object value) throws IOException {
        setValueIfNotExists(defaultConfig, path, value);
    }

    public void setValueIfNotExists(String fileName, String path, Object value) throws IOException {
        File file = new File(getDatafolder(), fileName + ".yml");
        if (!file.exists()) file.createNewFile();
        YamlConfiguration yml = getConfig(fileName, file);
        @Nullable ConfigurationSection config = yml.getConfigurationSection(defaultConfig);
        if (config == null) {
            initializeYml();
            config = yml.getConfigurationSection(defaultConfig);
            if (config == null) throw new IOException("Error creating the config for " + getName());
        }
        if (config.get(path) == null) {
            config.set(path, value);
            yml.save(file);
        }
    }

    public void setValue(String path, Object value) throws IOException {
        setValue(defaultConfig, path, value);
    }


    public void setValue(String fileName, String path, Object value) throws IOException {
        File file = new File(getDatafolder(), fileName + ".yml");
        if (!file.exists()) file.createNewFile();
        YamlConfiguration yml = getConfig(fileName, file);
        @Nullable ConfigurationSection config = yml.getConfigurationSection(defaultConfig);
        if (config == null) {
            initializeYml();
            config = yml.getConfigurationSection(defaultConfig);
            if (config == null) throw new IOException("Error creating the config for " + getName());
        }
        config.set(path, value);
        yml.save(file);
    }

    @Nullable
    public Object getValue(String path) throws IOException {
        return getValue(defaultConfig, path);
    }

    @Nullable
    protected Object getValue(String fileName, String path) throws IOException {
        File file = new File(getDatafolder(), fileName + ".yml");
        if (!file.exists()) file.createNewFile();
        YamlConfiguration yml = getConfig(fileName, file);
        @Nullable ConfigurationSection config = yml.getConfigurationSection(defaultConfig);
        if (config == null) {
            config = yml.createSection(defaultConfig);
            yml.save(file);
        }
        return config.get(path);
    }

    @NotNull
    private YamlConfiguration getConfig(String fileName, File file) throws IOException {
        YamlConfiguration yml = ymls.get(fileName);
        if (yml == null || !file.exists()) {
            // create the yml
            if (!file.exists()) file.createNewFile();
            yml = YamlConfiguration.loadConfiguration(file);
            ymls.put(fileName, yml);
        }
        return yml;
    }

    @NotNull
    public Object getValueOrInit(String path) throws IOException {
        return getValueOrInit(defaultConfig, path);
    }

    @NotNull
    public Object getValueOrInit(String fileName, String path) throws IOException {
        @Nullable Object value = getValue(fileName, path);
        if (value == null) initializeYml();
        value = getValue(path);
        if (value == null) throw new IOException("Error initializing config " + getName());
        return value;
    }

    public List<UUID> getMobs() {
        List<UUID> mobs = null;
        try {
            mobs = MobListSql.getMobs(getName());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        if (mobs == null) return Collections.emptyList();
        return mobs;
    }

    public void addMobs(UUID uuid) {
        try {
            MobListSql.addMob(getName(), uuid);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void registerInDB() {
        try {
            MobListSql.registerName(getName());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public File getDatafolder() {
        if (this.folder == null) {
            File folder = new File(MobTickPlugin.get().getDataFolder(), getName());
            if (!folder.exists()) folder.mkdirs();
            this.folder = folder;
        }
        return this.folder;
    }
}
