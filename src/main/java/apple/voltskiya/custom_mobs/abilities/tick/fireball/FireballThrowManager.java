package apple.voltskiya.custom_mobs.abilities.tick.fireball;

import apple.voltskiya.custom_mobs.abilities.MobTickPlugin;
import apple.voltskiya.custom_mobs.abilities.tick.parent.MobEntityEater;
import apple.voltskiya.custom_mobs.abilities.tick.parent.MobTickManagerTicker;
import apple.voltskiya.custom_mobs.abilities.tick.parent.MobTickerConfig;

public class FireballThrowManager extends MobEntityEater<FireballThrowConfigHolder> {

    public FireballThrowManager() {
        super(MobTickPlugin.get());
    }

    @Override
    public Class<? extends FireballThrowConfigHolder> getConfigClass() {
        return FireballThrowConfigHolder.class;
    }

    @Override
    public String getParentName() {
        return "fireball_throw";
    }

    @Override
    public <Config extends MobTickerConfig> MobTickManagerTicker<Config> createTicker(
        Config config) {
        return new FireballThrowTicker<>(config);
    }
}
