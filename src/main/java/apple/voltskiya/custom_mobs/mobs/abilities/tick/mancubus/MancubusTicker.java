package apple.voltskiya.custom_mobs.mobs.abilities.tick.mancubus;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobTickManagerTicker;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobTickerConfig;

public class MancubusTicker<Config extends MobTickerConfig> extends MobTickManagerTicker<Config> {
    public MancubusTicker(Config config) {
        super(config);
    }

    @Override
    public String getName() {
        return "mancubus";
    }

    @Override
    protected boolean isOnlyMobs() {
        return true;
    }
}
