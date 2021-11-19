package apple.voltskiya.custom_mobs.mobs.abilities.tick.fireball.config;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobTickerConfig;
import ycm.yml.manager.fields.YcmField;

public abstract class FireballThrowConfig implements MobTickerConfig {
    @YcmField
    public int spellCooldown = 8 * 20;
    @YcmField
    public double minSight = 4d;
    @YcmField
    public double maxSight = 100000;
    @YcmField
    public double shotSpeed = 1d;
    @YcmField
    public double throwChance = .05;
}
