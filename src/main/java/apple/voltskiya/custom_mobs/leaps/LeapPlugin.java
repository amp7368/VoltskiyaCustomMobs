package apple.voltskiya.custom_mobs.leaps;

import apple.voltskiya.custom_mobs.leaps.config.LeapConfigManager;
import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;

import java.io.IOException;

public class LeapPlugin extends PluginManagedModule {
    private static PluginManagedModule instance;

    public static PluginManagedModule get() {
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
        return "Leaping";
    }
}
