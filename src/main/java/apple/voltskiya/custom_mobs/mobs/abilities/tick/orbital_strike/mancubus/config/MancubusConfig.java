package apple.voltskiya.custom_mobs.mobs.abilities.tick.orbital_strike.mancubus.config;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobTickerConfig;

public abstract class MancubusConfig implements MobTickerConfig {

    public int spellCooldown = 20 * 20;

    public int initialDelay = 40;

    public int burstDelay1 = 10;

    public int burstDelay2 = 10;

    public int burstDelay3 = 10;

    public double minSight = 3d;

    public double maxSight = 1000;

    public double shotSpeed = 0.6;

    public double burst1Angle1 = 0;

    public double burst1Angle2 = 10;

    public double burst2Angle1 = 0;

    public double burst2Angle2 = -10;

    public double burst3Angle1 = -15;

    public double burst3Angle2 = 15;
}
