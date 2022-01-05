package apple.voltskiya.custom_mobs.mobs.abilities.tick.parent;

import apple.voltskiya.custom_mobs.mobs.abilities.MobTickPlugin;
import apple.voltskiya.mob_manager.parent.listen.MMSpawnListener;
import voltskiya.apple.configs.plugin.manage.PluginManagedModuleConfig;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class MobEntityEater<Holder extends MobTickerConfigHolder<?>> implements ConfigHolderSupplier<Holder> {
    private final Map<String, MobTickManagerTicker<?>> tickers;

    public MobEntityEater() {
        this.tickers = new HashMap<>();
        for (Map.Entry<String, ? extends MobTickerConfig> config : getConfigHolder().getConfigurationsMap().entrySet()) {
            MobTickManagerTicker<? extends MobTickerConfig> ticker = createTicker(config.getValue());
            this.tickers.put(config.getKey(), ticker);
            MMSpawnListener.get().addListener(ticker);
        }
    }

    public abstract <Config extends MobTickerConfig> MobTickManagerTicker<Config> createTicker(Config config);

    public Collection<String> getTags() {
        return tickers.keySet();
    }

    public PluginManagedModuleConfig getModule() {
        return MobTickPlugin.get();
    }
}
