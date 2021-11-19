package apple.voltskiya.custom_mobs.mobs.abilities.tick.fireball;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobTickManagerTicker;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobTickerConfig;

public class FireballThrowTicker<Config extends MobTickerConfig> extends MobTickManagerTicker<Config> {
    public FireballThrowTicker(Config config) {
        super(config);
    }

    @Override
    public String getName() {
        return "fireball_throw";
    }

    @Override
    protected boolean isOnlyMobs() {
        return true;
    }
}
