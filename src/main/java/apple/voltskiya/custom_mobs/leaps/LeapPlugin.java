package apple.voltskiya.custom_mobs.leaps;

import apple.voltskiya.custom_mobs.ConfigManager;
import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.leaps.config.LeapConfigManager;
import apple.voltskiya.custom_mobs.leaps.hellish_catalyst.LeapHellishCatalyst;
import apple.voltskiya.custom_mobs.leaps.pounce.LeapPounceNormal;
import apple.voltskiya.custom_mobs.leaps.pounce.LeapPounceUpwards;
import apple.voltskiya.custom_mobs.leaps.revenant.LeapRevenant;
import apple.voltskiya.custom_mobs.leaps.upwards.LeapUpwards;

import java.io.IOException;
import java.util.Arrays;

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
            Arrays.asList(new LeapUpwards(),
                    new LeapHellishCatalyst(),
                    new LeapPounceNormal(),
                    new LeapPounceUpwards(),
                    new LeapRevenant()).forEach(ConfigManager::registerInDB);
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
