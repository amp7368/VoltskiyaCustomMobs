package apple.voltskiya.custom_mobs.abilities.common.sweb;

import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.ability.MMAbility;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public class SWebMob extends MMAbility<SWebConfig> {

    public SWebMob(MMSpawned mob, SWebConfig config) {
        super(mob, config);
    }


    @Override
    protected void startAbility() {
        LivingEntity target = getTarget();
        Location targetLocation = target.getLocation();

        new SWebThrow(config, getEyeLocation(), targetLocation);

        finishAbility();
    }

    @Override
    protected boolean canStartAbility() {
        return hasTarget();
    }

    @Override
    public void cleanUp(boolean b) {
    }
}
