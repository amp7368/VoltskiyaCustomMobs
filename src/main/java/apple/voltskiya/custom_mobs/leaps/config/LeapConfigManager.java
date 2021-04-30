package apple.voltskiya.custom_mobs.leaps.config;

import apple.voltskiya.custom_mobs.ConfigManager;
import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.leaps.LeapPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class LeapConfigManager extends ConfigManager {
    private final Map<String, LeapConfig> leapTypeNames = new HashMap<>();
    private static LeapConfigManager instance;

    public LeapConfigManager() throws IOException {
        instance = this;
        this.initializeYml();
        File[] leapFiles = this.getDatafolder().listFiles();
        if (leapFiles != null) {
            for (File leapFile : leapFiles) {
                @NotNull YamlConfiguration configMain = getConfig(leapFile.getName(), leapFile);
                @NotNull Set<String> typeNames = configMain.getKeys(false);
                for (String leapTypeName : typeNames) {
                    ConfigurationSection config = configMain.getConfigurationSection(leapTypeName);
                    if (config != null){
                        this.leapTypeNames.put(leapTypeName, LeapYmlSettings.getLeapConfig(config));
                    }
                }
            }
        }
    }

    public static LeapConfigManager get() {
        return instance;
    }

    @Nullable
    public final LeapConfig getLeap(String name) {
        return leapTypeNames.get(name);
    }

    @Override
    public String getName() {
        return "leaping";
    }

    @Override
    public apple.voltskiya.custom_mobs.YmlSettings[] getSettings() {
        return LeapYmlSettings.values();
    }

    @Override
    protected VoltskiyaModule getPlugin() {
        return LeapPlugin.get();
    }

    private enum LeapYmlSettings implements apple.voltskiya.custom_mobs.YmlSettings {
        TIME_FULL("time_full", 17),
        LEAP_PEAK("leap_peak", 6),
        DISTANCE_MIN("distance_min", 5.0),
        DISTANCE_MAX("distance_max", 12.0),
        LEAP_CHECK_INTERVAL("leap_check_interval", 20);

        private final String path;
        private final Object value;

        LeapYmlSettings(String path, Object value) {
            this.path = path;
            this.value = value;
        }

        public static LeapConfig getLeapConfig(ConfigurationSection config) {
            return new LeapConfig(
                    config.getDouble(TIME_FULL.path),
                    config.getDouble(LEAP_PEAK.path),
                    config.getDouble(DISTANCE_MIN.path),
                    config.getDouble(DISTANCE_MAX.path),
                    config.getInt(LEAP_CHECK_INTERVAL.path)
            );
        }

        @Override
        public String getPath() {
            return path;
        }

        @Override
        public Object getValue() {
            return value;
        }
    }
}