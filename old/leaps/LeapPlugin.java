package apple.voltskiya.custom_mobs.leaps;

import apple.voltskiya.custom_mobs.leaps.config.LeapConfigManager;
import apple.mc.utilities.PluginModule;

import java.io.IOException;

public class LeapPlugin extends PluginModule {
    private static PluginModule instance;

    public static PluginModule get() {
        return instance;
    }

    @Override
    public void init() {
        instance = this;
    }

    @Override
    public void enable() {
        try {
            new LeapConfigManager();
            new LeapSpawnListener();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "leaping";
    }
}
