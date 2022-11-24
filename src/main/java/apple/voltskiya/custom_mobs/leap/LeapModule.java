package apple.voltskiya.custom_mobs.leap;

import apple.voltskiya.custom_mobs.leap.basic.LeapBasicConfig;
import apple.voltskiya.custom_mobs.leap.parent.LeapSpawner;
import apple.voltskiya.custom_mobs.leap.pounce.LeapPounceConfig;
import apple.voltskiya.custom_mobs.leap.revenant.LeapRevenantConfig;
import com.voltskiya.lib.AbstractModule;
import com.voltskiya.lib.configs.factory.AppleConfigLike;
import java.util.List;

public class LeapModule extends AbstractModule {

    private static LeapModule instance;

    public static LeapModule get() {
        return instance;
    }

    public LeapModule() {
        instance = this;
    }

    @Override
    public void enable() {
        new LeapSpawner<>(LeapBasicConfig::new, LeapBasicConfig.class, "");
        new LeapSpawner<>(LeapPounceConfig::new, LeapPounceConfig.class, "pounce");
        new LeapSpawner<>(LeapRevenantConfig::new, LeapRevenantConfig.class, "revenant");
    }

    @Override
    public String getName() {
        return "Leap";
    }

    @Override
    public List<AppleConfigLike> getConfigs() {
        return List.of();
    }
}
