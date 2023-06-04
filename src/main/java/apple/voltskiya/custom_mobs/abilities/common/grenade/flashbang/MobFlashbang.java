package apple.voltskiya.custom_mobs.abilities.common.grenade.flashbang;

import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.ability.MMAbility;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class MobFlashbang extends MMAbility<FlashbangConfig> {

    public MobFlashbang(MMSpawned mob, FlashbangConfig flashbangConfig) {
        super(mob, flashbangConfig);
    }

    @Override
    protected void startAbility() {
        Location startLocation = getMob().getEyeLocation();
        Vector velocity = startLocation.getDirection();
        startLocation.add(velocity);
        velocity.multiply(config.velocity());
        new ThrowFlashbang(config).start(startLocation, velocity, 60);
    }

    @Override
    protected boolean canStartAbility() {
        LivingEntity target = getTarget();
        if (target == null) return false;
        return getMob().hasLineOfSight(target);
    }

    @Override
    public void cleanUp(boolean b) {

    }
}
