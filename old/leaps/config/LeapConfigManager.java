package apple.voltskiya.custom_mobs.leaps.config;

import com.voltskiya.lib.pmc.AbstractModule;
import apple.voltskiya.custom_mobs.leaps.LeapPlugin;
import apple.voltskiya.custom_mobs.leaps.misc.LeapSpecificMisc;
import apple.voltskiya.custom_mobs.nms.parent.config.ConfigManager;
import apple.voltskiya.custom_mobs.nms.parent.config.YmlSettings;
import apple.voltskiya.custom_mobs.sql.MobListSql;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.entity.CraftMob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;


public class LeapConfigManager extends ConfigManager {

    private static LeapConfigManager instance;
    private final Map<String, LeapPreConfig> leapTypeNames = new HashMap<>();

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
                    if (config != null) {
                        this.leapTypeNames.put(leapTypeName, LeapYmlSettings.getLeapConfig(config));
                    }
                }
            }
        }
        registerLeaps();
    }

    public static LeapConfigManager get() {
        return instance;
    }

    @Nullable
    public static LeapPreConfig getLeap(String name) {
        return get().leapTypeNames.get(name);
    }

    private void registerLeaps() {
        for (Map.Entry<String, LeapPreConfig> leapType : leapTypeNames.entrySet()) {
            try {
                MobListSql.registerName(leapType.getKey());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            List<UUID> uuids = null;
            try {
                uuids = MobListSql.getMobs(getName());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            if (uuids == null) continue;
            for (UUID uuid : uuids) {
                org.bukkit.entity.Entity mob = Bukkit.getEntity(uuid);
                if (mob instanceof CraftMob)
                    LeapSpecificMisc.eatEntity(((CraftMob) mob).getHandle(), leapType.getValue());
                else MobListSql.removeMob(uuid);
            }
        }
    }

    @Override
    public String getName() {
        return "leaping";
    }

    @Override
    public YmlSettings[] getSettings() {
        return LeapYmlSettings.values();
    }

    @Override
    protected AbstractModule getPlugin() {
        return LeapPlugin.get();
    }

    private enum LeapYmlSettings implements YmlSettings {
        TIME_FULL("time_full", 17),
        PEAK("peak", 6),
        DISTANCE_MIN("distance_min", 5.0),
        DISTANCE_MAX("distance_max", 12.0),
        CHECK_INTERVAL("check_interval", 20),
        COOLDOWN("cooldown", 20);

        private final String path;
        private final Object value;

        LeapYmlSettings(String path, Object value) {
            this.path = path;
            this.value = value;
        }

        public static LeapPreConfig getLeapConfig(ConfigurationSection config) {
            return new LeapPreConfig(
                config.getDouble(TIME_FULL.path),
                config.getDouble(PEAK.path),
                config.getDouble(DISTANCE_MIN.path),
                config.getDouble(DISTANCE_MAX.path),
                config.getInt(CHECK_INTERVAL.path),
                config.getInt(COOLDOWN.path)
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