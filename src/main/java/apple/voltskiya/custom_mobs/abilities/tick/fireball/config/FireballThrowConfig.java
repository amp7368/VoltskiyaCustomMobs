package apple.voltskiya.custom_mobs.abilities.tick.fireball.config;

import apple.voltskiya.custom_mobs.abilities.tick.parent.MobTickerConfig;

public abstract class FireballThrowConfig implements MobTickerConfig {

    public int spellCooldown = 8 * 20;
    public double minSight = 4d;
    public double maxSight = 100000;
    public double shotSpeed = 1d;
    public double throwChance = .05;
}
