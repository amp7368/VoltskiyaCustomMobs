package apple.voltskiya.custom_mobs.abilities.tick.parent;

import apple.lib.pmc.AppleModule;
import apple.voltskiya.mob_manager.listen.MMSpawnListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class MobEntityEater<Holder extends MobTickerConfigHolder<?>> implements
    ConfigHolderSupplier<Holder> {

    private final Map<String, MobTickManagerTicker<?>> tickers;
    private AppleModule module;

    public MobEntityEater(AppleModule module) {
        this.module = module;
        this.tickers = new HashMap<>();
        for (Map.Entry<String, ? extends MobTickerConfig> config : getConfigHolder().getConfigurationsMap()
            .entrySet()) {
            MobTickManagerTicker<? extends MobTickerConfig> ticker = createTicker(
                config.getValue());
            this.tickers.put(config.getKey(), ticker);
            MMSpawnListener.get().addListener(ticker);
        }
    }

    public abstract <Config extends MobTickerConfig> MobTickManagerTicker<Config> createTicker(
        Config config);

    public Collection<String> getTags() {
        return tickers.keySet();
    }

    @Override
    public AppleModule getModule() {
        return module;
    }
}
