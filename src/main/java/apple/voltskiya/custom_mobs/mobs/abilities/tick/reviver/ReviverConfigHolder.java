package apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobTickerConfigHolder;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.config.ReviverConfig;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.config.ReviverConfigBasic;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.config.ReviverConfigPulse;


import java.util.Collection;
import java.util.List;

public class ReviverConfigHolder implements MobTickerConfigHolder<ReviverConfig> {
    
    public ReviverConfigBasic basic = new ReviverConfigBasic();
    
    public ReviverConfigPulse pulse = new ReviverConfigPulse();

    @Override
    public Collection<ReviverConfig> getConfigurations() {
        return List.of(basic, pulse);
    }
}
