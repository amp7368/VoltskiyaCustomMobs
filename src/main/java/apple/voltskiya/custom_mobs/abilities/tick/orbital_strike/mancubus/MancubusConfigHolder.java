package apple.voltskiya.custom_mobs.abilities.tick.orbital_strike.mancubus;

import apple.voltskiya.custom_mobs.abilities.tick.parent.MobTickerConfigHolder;
import apple.voltskiya.custom_mobs.abilities.tick.orbital_strike.mancubus.config.MancubusConfig;
import apple.voltskiya.custom_mobs.abilities.tick.orbital_strike.mancubus.config.MancubusConfigBasic;

import java.util.Collection;
import java.util.List;

public class MancubusConfigHolder implements MobTickerConfigHolder<MancubusConfig> {
    
    public MancubusConfigBasic basic = new MancubusConfigBasic();

    @Override
    public Collection<? extends MancubusConfig> getConfigurations() {
        return List.of(basic);
    }
}
