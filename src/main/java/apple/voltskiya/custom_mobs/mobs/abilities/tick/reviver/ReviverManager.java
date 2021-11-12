package apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobEntityEater;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobTickerConfig;

public class ReviverManager extends MobEntityEater<ReviverConfigHolder> {
    public <Config extends MobTickerConfig> ReviverTicker<Config> createTicker(Config config) {
        return new ReviverTicker<>(config);
    }

    @Override
    public Class<ReviverConfigHolder> getConfigClass() {
        return ReviverConfigHolder.class;
    }

    @Override
    public String getParentName() {
        return "reviver";
    }
}
