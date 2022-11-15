package apple.voltskiya.custom_mobs.abilities.common.reviver.config;

import apple.voltskiya.custom_mobs.abilities.common.reviver.mob.MobReviverPulse;
import apple.voltskiya.mob_manager.mob.MMSpawned;


public class ReviverConfigPulse extends ReviverConfig {

    public int ticksToChargeUp = 2 * 20;

    public int ticksForAnger = 2 * 20;

    public int ticksForPulse = 3 * 20;

    public int pulseRadius = 10;

    public int deadTooLong = 1000 * 60 * 10;


    @Override
    public void doSpawn(MMSpawned mob) {
        new MobReviverPulse(mob, this);
    }

    @Override
    public String getBriefTag() {
        return "reviver.pulse";
    }
}
