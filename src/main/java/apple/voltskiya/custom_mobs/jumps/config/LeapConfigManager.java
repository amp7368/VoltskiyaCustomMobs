package apple.voltskiya.custom_mobs.jumps.config;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.jumps.LeapPlugin;
import apple.voltskiya.custom_mobs.jumps.tick.LeapListenerTemp;
import apple.voltskiya.custom_mobs.mob_tick.tick.ConfigManager;
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

    public LeapConfigManager() throws IOException {
        initializeYml();
        @NotNull YamlConfiguration configMain = getConfig("leaps", getDatafolder());
        configMain.getConfigurationSection("main");
        @NotNull Set<String> typeNames = configMain.getKeys(false);
        for (String leapTypeName : typeNames) {
            ConfigurationSection config = configMain.getConfigurationSection(leapTypeName);
            if (config != null)
                this.leapTypeNames.put(leapTypeName, YmlSettings.getLeapConfig(config));
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
    public void initializeYml() throws IOException {
        setValueIfNotExists("leaps", "leap_example." + YmlSettings.LEAP_TIME.path, 20);
        setValueIfNotExists("leaps", "leap_example." + YmlSettings.LEAP_PEAK.path, 5);
        setValueIfNotExists("leaps", "leap_example." + YmlSettings.DISTANCE_MIN.path, 10);
        setValueIfNotExists("leaps", "leap_example." + YmlSettings.DISTANCE_MAX.path, 20);
    }

    @Override
    protected VoltskiyaModule getPlugin() {
        return LeapPlugin.get();
    }

    private enum YmlSettings {
        LEAP_TIME("leap_time"),
        LEAP_PEAK("leap_peak"),
        DISTANCE_MIN("distance_min"),
        DISTANCE_MAX("distance_max");

        private final String path;

        YmlSettings(String path) {
            this.path = path;
        }

        public static LeapConfig getLeapConfig(ConfigurationSection config) {
            return new LeapConfig(
                    config.getInt(LEAP_TIME.path),
                    config.getDouble(LEAP_PEAK.path),
                    config.getInt(DISTANCE_MIN.path),
                    config.getInt(DISTANCE_MAX.path)
            );
        }
    }
}