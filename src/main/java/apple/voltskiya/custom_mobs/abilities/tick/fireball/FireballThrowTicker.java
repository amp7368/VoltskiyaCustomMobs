package apple.voltskiya.custom_mobs.abilities.tick.fireball;

import apple.voltskiya.custom_mobs.abilities.tick.parent.MobTickManagerTicker;
import apple.voltskiya.custom_mobs.abilities.tick.parent.MobTickerConfig;

public class FireballThrowTicker<Config extends MobTickerConfig> extends MobTickManagerTicker<Config> {
    public FireballThrowTicker(Config config) {
        super(config);
    }

    public boolean isOnlyMobs() {
        return true;
    }
}
