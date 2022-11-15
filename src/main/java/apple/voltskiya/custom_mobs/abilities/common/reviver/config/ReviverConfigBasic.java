package apple.voltskiya.custom_mobs.abilities.common.reviver.config;

import apple.voltskiya.custom_mobs.abilities.common.reviver.mob.MobReviverBasic;
import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.mob.MMSpawned;


public class ReviverConfigBasic extends ReviverConfig implements SpawnListener {

    public int reviveRitualTime = 20;

    public double searchRadius = 50;

    public int deadCooldown = 1000 * 5;

    public int deadTooLong = 1000 * 60 * 10;
    public int giveUpTick = 20 * 20;

    @Override
    public String getBriefTag() {
        return "reviver.basic";
    }

    @Override
    public void doSpawn(MMSpawned mob) {
        new MobReviverBasic(mob, this);
    }
}
