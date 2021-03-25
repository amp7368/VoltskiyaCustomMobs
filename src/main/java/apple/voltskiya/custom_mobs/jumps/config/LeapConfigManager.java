package apple.voltskiya.custom_mobs.jumps.config;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.jumps.LeapPlugin;
import apple.voltskiya.custom_mobs.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class LeapConfigManager extends ConfigManager {
    private final Map<String, LeapConfig> leapTypeNames = new HashMap<>();

    public LeapConfigManager() throws IOException {
        initializeYml();
        @NotNull YamlConfiguration configMain = getConfig("leaps", getDatafolder());
        configMain.getConfigurationSection("main");
        @NotNull Set<String> typeNames = configMain.getKeys(false);
        for (String leapTypeName : typeNames) {
            ConfigurationSection config = configMain.getConfigurationSection(leapTypeName);
            if (config != null)
                this.leapTypeNames.put(leapTypeName, LeapYmlSettings.getLeapConfig(config));
        }
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
    public void initializeYml() throws IOException {
        setValueIfNotExists("leaps", "leap_example." + LeapYmlSettings.LEAP_TIME.path, 20);
        setValueIfNotExists("leaps", "leap_example." + LeapYmlSettings.LEAP_PEAK.path, 5);
        setValueIfNotExists("leaps", "leap_example." + LeapYmlSettings.DISTANCE_MIN.path, 10);
        setValueIfNotExists("leaps", "leap_example." + LeapYmlSettings.DISTANCE_MAX.path, 20);
    }

    @Override
    protected VoltskiyaModule getPlugin() {
        return LeapPlugin.get();
    }

    private enum LeapYmlSettings implements apple.voltskiya.custom_mobs.YmlSettings {
        LEAP_TIME("leap_time", 20),
        LEAP_PEAK("leap_peak", 40),
        DISTANCE_MIN("distance_min", 20),
        DISTANCE_MAX("distance_max", 50);

        private final String path;
        private final Object value;

        LeapYmlSettings(String path, Object value) {
            this.path = path;
            this.value = value;
        }

        public static LeapConfig getLeapConfig(ConfigurationSection config) {
            return new LeapConfig(
                    config.getInt(LEAP_TIME.path),
                    config.getDouble(LEAP_PEAK.path),
                    config.getInt(DISTANCE_MIN.path),
                    config.getInt(DISTANCE_MAX.path)
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