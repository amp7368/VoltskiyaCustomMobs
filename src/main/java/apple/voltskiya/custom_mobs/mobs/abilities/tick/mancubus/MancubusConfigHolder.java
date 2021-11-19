package apple.voltskiya.custom_mobs.mobs.abilities.tick.mancubus;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.mancubus.config.MancubusConfig;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.mancubus.config.MancubusConfigBasic;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobTickerConfigHolder;
import ycm.yml.manager.fields.YcmField;

import java.util.Collection;
import java.util.List;

public class MancubusConfigHolder implements MobTickerConfigHolder<MancubusConfig> {
    @YcmField
    public MancubusConfigBasic basic = new MancubusConfigBasic();

    @Override
    public Collection<? extends MancubusConfig> getConfigurations() {
        return List.of(basic);
    }
}
