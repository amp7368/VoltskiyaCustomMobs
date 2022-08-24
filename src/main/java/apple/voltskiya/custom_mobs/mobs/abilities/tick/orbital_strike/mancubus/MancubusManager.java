package apple.voltskiya.custom_mobs.mobs.abilities.tick.orbital_strike.mancubus;

import apple.voltskiya.custom_mobs.mobs.abilities.MobTickPlugin;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobEntityEater;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobTickManagerTicker;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobTickerConfig;

public class MancubusManager extends MobEntityEater<MancubusConfigHolder> {

    public MancubusManager() {
        super(MobTickPlugin.get());
    }

    @Override
    public Class<? extends MancubusConfigHolder> getConfigClass() {
        return MancubusConfigHolder.class;
    }

    @Override
    public String getParentName() {
        return "mancubus";
    }

    @Override
    public <Config extends MobTickerConfig> MobTickManagerTicker<Config> createTicker(
        Config config) {
        return new MancubusTicker<>(config);
    }
}
