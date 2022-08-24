package apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobTickManagerTicker;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobTickerConfig;

public class ReviverTicker<Config extends MobTickerConfig> extends MobTickManagerTicker<Config> {
    public ReviverTicker(Config config) {
        super(config);
    }

    @Override
    public boolean isOnlyMobs() {
        return true;
    }
}
