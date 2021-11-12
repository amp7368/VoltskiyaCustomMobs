package apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobTickerConfigHolder;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.config.ReviverConfig;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.config.ReviverConfigBasic;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.config.ReviverConfigPulse;
import ycm.yml.manager.fields.YcmField;

import java.util.Collection;
import java.util.List;

public class ReviverConfigHolder implements MobTickerConfigHolder<ReviverConfig> {
    @YcmField
    public ReviverConfigBasic basic = new ReviverConfigBasic();
    @YcmField
    public ReviverConfigPulse pulse = new ReviverConfigPulse();

    @Override
    public Collection<ReviverConfig> getConfigurations() {
        return List.of(basic, pulse);
    }
}
