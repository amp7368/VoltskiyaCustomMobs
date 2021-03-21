package apple.voltskiya.custom_mobs.jumps;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.jumps.config.LeapConfigManager;
import apple.voltskiya.custom_mobs.jumps.listeners.LeapDeathListener;
import apple.voltskiya.custom_mobs.jumps.listeners.LeapSpawnListener;
import apple.voltskiya.custom_mobs.jumps.tick.LeapListenerTemp;
import apple.voltskiya.custom_mobs.jumps.tick.LeapTickerManager;

import java.io.IOException;

public class LeapPlugin extends VoltskiyaModule {
    private static VoltskiyaModule instance;

    public static VoltskiyaModule get() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;
        try {
            new LeapConfigManager();
            new LeapTickerManager();
            new LeapDeathListener();
            new LeapSpawnListener();
            new LeapListenerTemp();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "Leaping";
    }
}
