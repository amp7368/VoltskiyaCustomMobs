package apple.voltskiya.custom_mobs.abilities.overseer.laser;

import apple.mc.utilities.world.vector.VectorUtils;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.ability.MMAbility;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;

public class MobMissileLaser<Config extends MissileLaserConfig> extends MMAbility<Config> {

    public MobMissileLaser(MMSpawned mob, Config config) {
        super(mob, config, config.activation());
        final AttributeInstance followRange = mob.getMob()
            .getAttribute(Attribute.GENERIC_FOLLOW_RANGE);
        if (followRange != null) {
            followRange.setBaseValue(Math.max(followRange.getBaseValue(), config.range));
        }
    }

    @Override
    protected void startAbility() {
        new MissileLaserSpell<>(this, this.config).stateChoice();
    }

    @Override
    protected boolean canStartAbility() {
        final LivingEntity target = getTarget();
        if (target == null)
            return false;
        return config.inRange(VectorUtils.distance(target.getLocation(), this.getLocation()));
    }


    @Override
    public void cleanUp(boolean isDead) {
    }

    public void finish() {
        this.finishAbility();
    }
}
