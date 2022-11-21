package apple.voltskiya.custom_mobs.abilities.overseer.laser;

import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.ability.MMAbilityConfig;
import apple.voltskiya.mob_manager.mob.ability.activation.Activation;
import apple.voltskiya.mob_manager.mob.ability.activation.ActivationRange;
import java.util.Collection;
import java.util.List;

public class MissileLaserConfig extends MMAbilityConfig {

    public double shotSpeed = .9;
    public int chargeUpTicks = 80;
    public int shotsToTake = 15;
    public int timeToShoot = 20;

    public ActivationRange range = new ActivationRange(50);

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

    @Override
    public Collection<Activation> getActivations() {
        return List.of(range);
    }
}
