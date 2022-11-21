package apple.voltskiya.custom_mobs.abilities.nether.fireball.mob;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.abilities.nether.fireball.config.FireballThrowConfig;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.ability.MMAbility;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.utilities.action.OneOffAction;
import voltskiya.apple.utilities.action.RepeatingActionManager;
import voltskiya.apple.utilities.minecraft.TagConstants;
import voltskiya.apple.utilities.sound.SoundActionImpl;
import voltskiya.apple.utilities.sound.SoundManager;

public class MobFireballThrow<Config extends FireballThrowConfig> extends MMAbility<Config> {

    private static final String CHARGE_UP = "charge_up";
    private static final String DO_THROW = "throw";
    private static final String SOUND_THROW = "throw";
    private static final String SOUND_WARNING = "warning";
    private final SoundManager soundManager = new SoundManager().registerSound(
        new SoundActionImpl(SOUND_THROW, Sound.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, .2f,
            1.2f)).registerSound(
        new SoundActionImpl(SOUND_WARNING, Sound.ENTITY_HUSK_HURT, SoundCategory.HOSTILE, 0.7f,
            1.4f));
    private final RepeatingActionManager actionManager = new RepeatingActionManager(
        VoltskiyaPlugin.get()).registerAction(new OneOffAction(CHARGE_UP, this::doChargeUp))
        .registerAction(new OneOffAction(DO_THROW, this::doThrow))
        .registerFinally(this::finishAbility);

    public MobFireballThrow(MMSpawned mob, Config config) {
        super(mob, config);
    }

    @Override
    protected void startAbility() {
        actionManager.start().startAction(CHARGE_UP);
    }

    @Override
    public void cleanUp(boolean isDead) {
        actionManager.stop();
    }

    @Override
    protected boolean canStartAbility() {
        return hasTarget();
    }


    private void doChargeUp() {
        soundManager.playSound(SOUND_THROW, getLocation());
        actionManager.scheduleActionAndStart(DO_THROW, 10);
    }

    private void doThrow() {
        @Nullable LivingEntity target = getTarget();
        if (target == null)
            return;
        Location location = getEyeLocation();
        @NotNull Vector direction = target.getEyeLocation().clone().subtract(location).toVector()
            .normalize();
        shootFireball(location, direction);
        soundManager.playSound(SOUND_WARNING, getLocation());
    }

    protected void shootFireball(Location location, @NotNull Vector direction) {
        @NotNull Entity entity = location.getWorld()
            .spawnEntity(location, EntityType.SMALL_FIREBALL,
                CreatureSpawnEvent.SpawnReason.CUSTOM);
        if (entity instanceof SmallFireball fireball) {
            fireball.setVelocity(direction.multiply(config.shotSpeed));
            fireball.setDirection(direction);
            fireball.addScoreboardTag(TagConstants.NO_COLLIDE_FIREBALL_THROW);
        }
    }

}
