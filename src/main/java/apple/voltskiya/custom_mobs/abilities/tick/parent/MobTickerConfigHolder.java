package apple.voltskiya.custom_mobs.abilities.tick.parent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface MobTickerConfigHolder<ConfigInterface extends MobTickerConfig> {
    default Map<String, ? extends MobTickerConfig> getConfigurationsMap() {
        Map<String, ConfigInterface> configs = new HashMap<>();
        for (ConfigInterface config : getConfigurations()) {
            configs.put(config.getTag(), config);
        }
        return configs;
    }

    Collection<? extends ConfigInterface> getConfigurations();
}
