package apple.voltskiya.custom_mobs.abilities.overseer.laser;

import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.ability.MMAbilityConfig;

public class MissileLaserConfig extends MMAbilityConfig {

    public double range = 50;
    public double shotSpeed = .9;
    public int chargeUpTicks = 80;
    public int shotsToTake = 15;
    public int timeToShoot = 20;

    public transient String tag;

    @Override
    public boolean isOnlyMobs() {
        return true;
    }

    @Override
    public void doSpawn(MMSpawned mmSpawned) {
        new MobMissileLaser<>(mmSpawned, this);
    }

    @Override
    public String getBriefTag() {
        return this.tag;
    }

    public boolean inRange(double distance) {
        return distance <= this.range;
    }
}
