package apple.voltskiya.custom_mobs.mobs.abilities.tick.fireball;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.fireball.config.*;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobTickerConfigHolder;
import ycm.yml.manager.fields.YcmField;

import java.util.Collection;
import java.util.List;

public class FireballThrowConfigHolder implements MobTickerConfigHolder<FireballThrowConfig> {
    @YcmField
    public final FireballThrowConfigBasic basic = new FireballThrowConfigBasic();
    @YcmField
    public final FireballThrowConfigRapid fireball_rapid = new FireballThrowConfigRapid();
    @YcmField
    public final FireballThrowConfigBasic3 basic3 = new FireballThrowConfigBasic3();
    @YcmField
    public final FireballThrowConfigBasic4 basic4 = new FireballThrowConfigBasic4();

    @Override
    public Collection<? extends FireballThrowConfig> getConfigurations() {
        return List.of(basic, fireball_rapid, basic3, basic4);
    }
}
