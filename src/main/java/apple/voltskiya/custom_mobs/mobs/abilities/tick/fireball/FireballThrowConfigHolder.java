package apple.voltskiya.custom_mobs.mobs.abilities.tick.fireball;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.fireball.config.FireballThrowConfig;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.fireball.config.FireballThrowConfigBasic;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.fireball.config.FireballThrowConfigBasic3;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.fireball.config.FireballThrowConfigBasic4;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.fireball.config.FireballThrowConfigRapid;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobTickerConfigHolder;
import java.util.Collection;
import java.util.List;

public class FireballThrowConfigHolder implements MobTickerConfigHolder<FireballThrowConfig> {

    public final FireballThrowConfigBasic basic = new FireballThrowConfigBasic();
    public final FireballThrowConfigRapid fireball_rapid = new FireballThrowConfigRapid();
    public final FireballThrowConfigBasic3 basic3 = new FireballThrowConfigBasic3();

    public final FireballThrowConfigBasic4 basic4 = new FireballThrowConfigBasic4();

    @Override
    public Collection<? extends FireballThrowConfig> getConfigurations() {
        return List.of(basic, fireball_rapid, basic3, basic4);
    }
}
