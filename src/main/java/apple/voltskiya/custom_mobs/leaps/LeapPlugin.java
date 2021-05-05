package apple.voltskiya.custom_mobs.leaps;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.leaps.config.LeapConfigManager;

import java.io.IOException;

public class LeapPlugin extends VoltskiyaModule {
    private static VoltskiyaModule instance;

    public static VoltskiyaModule get() {
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
