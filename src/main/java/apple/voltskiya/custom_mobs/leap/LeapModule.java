package apple.voltskiya.custom_mobs.leap;

import apple.voltskiya.custom_mobs.leap.basic.LeapBasicSpawner;
import apple.voltskiya.custom_mobs.leap.pounce.LeapPounceSpawner;
import apple.voltskiya.custom_mobs.leap.revenant.LeapRevenantSpawner;
import apple.voltskiya.mob_manager.listen.SpawnListenerHolder;
import com.voltskiya.lib.AbstractModule;
import com.voltskiya.lib.configs.data.config.AppleConfig;
import com.voltskiya.lib.configs.data.config.AppleConfig.Builder;
import com.voltskiya.lib.configs.factory.AppleConfigLike;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LeapModule extends AbstractModule {

    private static LeapModule instance;

    public static LeapModule get() {
        return instance;
    }

    private final Collection<AppleConfig<? extends SpawnListenerHolder>> configs = new ArrayList<>();

    public LeapModule() {
        instance = this;
    }

    @Override
    public void enable() {
        configs.stream().map(AppleConfig::getInstance).forEach(SpawnListenerHolder::registerListeners);
        new LeapBasicSpawner();
    }

    @Override
    public String getName() {
        return "Leap";
    }

    @Override
    public List<AppleConfigLike> getConfigs() {
        return List.of(add(configJson(LeapPounceSpawner.class, "PounceConfig", "Pounce")),
            add(configJson(LeapRevenantSpawner.class, "RevenantConfig", "Revenant")));
    }

    private AppleConfigLike add(Builder<? extends SpawnListenerHolder> builder) {
        this.configs.add(builder.getConfig());
        return builder;
    }
}
