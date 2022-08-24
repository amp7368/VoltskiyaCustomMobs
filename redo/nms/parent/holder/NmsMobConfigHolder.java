package apple.voltskiya.custom_mobs.mobs.nms.parent.holder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface NmsMobConfigHolder<ConfigInterface extends NmsMobConfig<?, ?>> {
    default Map<String, ? extends ConfigInterface> getConfigurationsMap() {
        Map<String, ConfigInterface> configs = new HashMap<>();
        for (ConfigInterface config : getConfigurations()) {
            configs.put(config.getTag(), config);
        }
        return configs;
    }

    Collection<? extends ConfigInterface> getConfigurations();
}
