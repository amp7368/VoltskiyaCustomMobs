package apple.voltskiya.custom_mobs.leaps;

import apple.voltskiya.custom_mobs.leaps.config.LeapConfigManager;
import apple.mc.utilities.AppleModule;

import java.io.IOException;

public class LeapPlugin extends AppleModule {
    private static AppleModule instance;

    public static AppleModule get() {
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
