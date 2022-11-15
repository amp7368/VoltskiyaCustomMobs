package apple.voltskiya.custom_mobs.nms.parent.holder;

import apple.voltskiya.custom_mobs.mobs.PluginNmsMobs;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.ConfigHolderSupplier;
import org.bukkit.event.entity.CreatureSpawnEvent;
import voltskiya.apple.configs.plugin.manage.AbstractModuleConfig;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class NmsMobEntityEater<ConfigInterface extends NmsMobConfig<?, ?>
        > implements ConfigHolderSupplier<NmsMobConfigHolder<ConfigInterface>> {
    private final Map<String, NmsMobRegisterConfigable<?, ?>> nmsMobs;

    public NmsMobEntityEater() {
        NmsMobConfigHolder<ConfigInterface> holder = getConfigHolder();
        this.nmsMobs = new HashMap<>();
        for (Map.Entry<String, ? extends ConfigInterface> config : holder.getConfigurationsMap().entrySet()) {
            NmsMobRegisterConfigable<?, ?> register = config.getValue().make();
            if (config.getValue().isSpawnable())
                this.nmsMobs.put(config.getKey(), register);
        }
    }

    public Collection<String> getTags() {
        return nmsMobs.keySet();
    }

    public void eatSpawnEvent(String tag, CreatureSpawnEvent event) {
        NmsMobRegisterConfigable<?, ?> nmsMob = this.nmsMobs.get(tag);
        if (nmsMob != null)
            nmsMob.eatSpawnEvent(event);
    }

    public AbstractModuleConfig getModule() {
        return PluginNmsMobs.get();
    }

}
