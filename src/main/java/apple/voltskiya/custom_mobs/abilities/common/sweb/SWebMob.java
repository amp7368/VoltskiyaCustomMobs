package apple.voltskiya.custom_mobs.abilities.common.sweb;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.ability.MMAbility;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

public class SWebMob extends MMAbility<SWebConfig> {

    public SWebMob(MMSpawned mob, SWebConfig config) {
        super(mob, config);
    }

    private static void playThrowSound(Location targetLocation) {
        World world = targetLocation.getWorld();
        world.playSound(targetLocation, Sound.BLOCK_AZALEA_PLACE, SoundCategory.HOSTILE, 5, 0.3f);

        VoltskiyaPlugin.get().scheduleSyncDelayedTask(
            () -> world.playSound(targetLocation, Sound.ENTITY_SPIDER_HURT, SoundCategory.HOSTILE, 2.5f, 0.5f),
            2);
    }

    @Override
    protected void startAbility() {
        LivingEntity target = getTarget();
        if (target == null) {
            finishAbility();
            return;
        }
        Location targetLocation = target.getLocation();

        new SWebThrow(config, getEyeLocation(), targetLocation);
        playThrowSound(targetLocation);

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
