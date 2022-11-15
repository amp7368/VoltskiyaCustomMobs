package apple.voltskiya.custom_mobs.leaps;

import apple.voltskiya.custom_mobs.leaps.config.LeapConfigManager;
import apple.mc.utilities.AbstractModule;

import java.io.IOException;

public class LeapPlugin extends AbstractModule {
    private static AbstractModule instance;

    public static AbstractModule get() {
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
