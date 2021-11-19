package apple.voltskiya.custom_mobs.mobs.abilities.tick.fireball;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.fireball.config.FireballThrowConfig;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.fireball.config.FireballThrowConfigBasic;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobTickerConfigHolder;

import java.util.Collection;
import java.util.List;

public class FireballThrowConfigHolder implements MobTickerConfigHolder<FireballThrowConfig> {
    private final FireballThrowConfig basic = new FireballThrowConfigBasic();

    @Override
    public Collection<? extends FireballThrowConfig> getConfigurations() {
        return List.of(basic);
    }
}
