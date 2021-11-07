package apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobConfig;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobEntityEater;

public class ReviverManager extends MobEntityEater<ReviverConfigHolder> {
    public <Config extends MobConfig> ReviverTicker<Config> createTicker(Config config) {
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
