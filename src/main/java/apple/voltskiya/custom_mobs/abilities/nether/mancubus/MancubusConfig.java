package apple.voltskiya.custom_mobs.abilities.nether.mancubus;

import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.ability.MMAbilityConfig;

public class MancubusConfig extends MMAbilityConfig {

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

    @Override
    public void doSpawn(MMSpawned mob) {
        new MobMancubus<>(mob, this);
    }

    @Override
    public boolean isOnlyMobs() {
        return true;
    }

    @Override
    public String getBriefTag() {
        return "mancubus.basic";
    }
}
