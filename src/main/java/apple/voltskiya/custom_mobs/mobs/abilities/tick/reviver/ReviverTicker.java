package apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobConfig;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobTickManagerTicker;

public class ReviverTicker<Config extends MobConfig> extends MobTickManagerTicker<Config> {
    public ReviverTicker(Config config) {
        super(config);
    }

    @Override
    public String getName() {
        return "reviver";
    }

    @Override
    protected boolean isOnlyMobs() {
        return true;
    }
}
