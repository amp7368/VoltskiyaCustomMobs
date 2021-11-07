package apple.voltskiya.custom_mobs.mobs.abilities.tick.parent;

import apple.voltskiya.custom_mobs.mobs.abilities.MobTickPlugin;
import org.bukkit.event.entity.CreatureSpawnEvent;
import voltskiya.apple.configs.plugin.manage.PluginManagedModuleConfig;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class MobEntityEater<Holder extends MobConfigHolder<?>> implements ConfigHolderSupplier<Holder> {
    private final Map<String, MobTickManagerTicker<?>> tickers;

    public MobEntityEater() {
        Holder configHolder = getConfigHolder();
        this.tickers = new HashMap<>();
        for (Map.Entry<String, ? extends MobConfig> config : configHolder.getConfigurationsMap().entrySet()) {
            MobTickManagerTicker<? extends MobConfig> ticker = createTicker(config.getValue());
            this.tickers.put(config.getKey(), ticker);
            ticker.registerInDB();
            ticker.eatMobs();
        }
    }

    public abstract <Config extends MobConfig> MobTickManagerTicker<Config> createTicker(Config config);

    public Collection<String> getTags() {
        return tickers.keySet();
    }

    public void eatEvent(String tag, CreatureSpawnEvent event) {
        MobTickManagerTicker<?> ticker = this.tickers.get(tag);
        if (ticker != null && ticker.shouldAccept(event.getEntity()))
            ticker.eatAndRegisterEvent(event);
    }

    public PluginManagedModuleConfig getModule() {
        return MobTickPlugin.get();
    }
}
