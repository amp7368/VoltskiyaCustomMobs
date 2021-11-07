package apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.config;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobConfig;
import ycm.yml.manager.fields.YcmField;

public abstract class ReviverConfig implements MobConfig {
    @YcmField
    public double reviveChance = .3d;
    @YcmField
    public int giveUpTick = 220;
}
