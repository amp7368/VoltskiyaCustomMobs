package apple.voltskiya.custom_mobs.abilities.overseer.laser;

import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.ability.MMAbility;

public class MobMissileLaser<Config extends MissileLaserConfig> extends MMAbility<Config> {

    public MobMissileLaser(MMSpawned mob, Config config) {
        super(mob, config);
    }

    @Override
    protected void startAbility() {
        new MissileLaserSpell<>(this, this.config).stateChoice();
    }

    @Override
    protected boolean canStartAbility() {
        return hasTarget();
    }


    @Override
    public void cleanUp(boolean isDead) {
    }

    public void finish() {
        this.finishAbility();
    }
}
