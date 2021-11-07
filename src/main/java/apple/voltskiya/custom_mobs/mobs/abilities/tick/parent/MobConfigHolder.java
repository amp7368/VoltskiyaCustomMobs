package apple.voltskiya.custom_mobs.mobs.abilities.tick.parent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface MobConfigHolder<ConfigInterface extends MobConfig> {
    default Map<String, ? extends MobConfig> getConfigurationsMap() {
        Map<String, ConfigInterface> configs = new HashMap<>();
        for (ConfigInterface config : getConfigurations()) {
            configs.put(config.getTag(), config);
        }
        return configs;
    }

    Collection<? extends ConfigInterface> getConfigurations();
}
