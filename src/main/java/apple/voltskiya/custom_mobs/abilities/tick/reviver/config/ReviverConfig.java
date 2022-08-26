package apple.voltskiya.custom_mobs.abilities.tick.reviver.config;

import apple.voltskiya.custom_mobs.abilities.tick.parent.MobTickerConfig;

public abstract class ReviverConfig implements MobTickerConfig {
    
    public double reviveChance = .3d;
    
    public int giveUpTick = 220;
}
