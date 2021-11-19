package apple.voltskiya.custom_mobs.mobs.abilities.tick.mancubus.config;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobTickerConfig;
import ycm.yml.manager.fields.YcmField;

public abstract class MancubusConfig implements MobTickerConfig {
    @YcmField
    public int spellCooldown = 20 * 20;
    @YcmField
    public int initialDelay = 40;
    @YcmField
    public int burstDelay1 = 10;
    @YcmField
    public int burstDelay2 = 10;
    @YcmField
    public int burstDelay3 = 10;
    @YcmField
    public double minSight = 3d;
    @YcmField
    public double maxSight = 1000;
    @YcmField
    public double shotSpeed = 0.6;
    @YcmField
    public double burst1Angle1 = 0;
    @YcmField
    public double burst1Angle2 = 10;
    @YcmField
    public double burst2Angle1 = 0;
    @YcmField
    public double burst2Angle2 = -10;
    @YcmField
    public double burst3Angle1 = -15;
    @YcmField
    public double burst3Angle2 = 15;
}
