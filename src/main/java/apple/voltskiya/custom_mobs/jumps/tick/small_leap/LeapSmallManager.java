package apple.voltskiya.custom_mobs.jumps.tick.small_leap;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.YmlSettings;
import apple.voltskiya.custom_mobs.jumps.LeapMobManager;
import apple.voltskiya.custom_mobs.jumps.LeapPlugin;
import apple.voltskiya.custom_mobs.mob_tick.tick.SpawnEater;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.io.IOException;

public class LeapSmallManager extends SpawnEater {
    private static LeapSmallManager instance;

    public LeapSmallManager() {
        instance = this;
    }

    @Override
    public void eatEvent(CreatureSpawnEvent event) {
        // this is a small leap mob
        LeapMobManager.register(event.getEntity(), LeapSmallMobListener.get());
    }

    @Override
    public String getName() {
        return "leap_small";
    }

    @Override
    public YmlSettings[] getSettings() {
        return YmlSettingsSmall.values();
    }

    @Override
    public void initializeYml() throws IOException {

    }

    @Override
    protected VoltskiyaModule getPlugin() {
        return LeapPlugin.get();
    }

    private enum YmlSettingsSmall implements YmlSettings {
        ;
        private final String path;
        private final Object value;

        YmlSettingsSmall(String path, Object value) {
            this.path = path;
            this.value = value;
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