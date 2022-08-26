package apple.voltskiya.custom_mobs.abilities.tick.fireball.mob;

import apple.mc.utilities.world.vector.VectorUtils;
import apple.utilities.util.NumberUtils;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.abilities.tick.fireball.config.FireballThrowConfig;
import apple.voltskiya.custom_mobs.abilities.tick.parent.MobToTick;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.utilities.action.OneOffAction;
import voltskiya.apple.utilities.action.RepeatingActionManager;
import voltskiya.apple.utilities.chance.ChanceRolling;
import voltskiya.apple.utilities.minecraft.TagConstants;
import voltskiya.apple.utilities.sound.SoundActionImpl;
import voltskiya.apple.utilities.sound.SoundManager;

public class MobFireballThrow<Config extends FireballThrowConfig> extends MobToTick<Config> {

    private static final String CHARGE_UP = "charge_up";
    private static final String DO_THROW = "throw";
    private static final String SOUND_THROW = "throw";
    private static final String SOUND_WARNING = "warning";
    private final ChanceRolling random;
    private final SoundManager soundManager = new SoundManager().registerSound(
        new SoundActionImpl(SOUND_THROW, Sound.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, .2f,
            1.2f)).registerSound(
        new SoundActionImpl(SOUND_WARNING, Sound.ENTITY_HUSK_HURT, SoundCategory.HOSTILE, 0.7f,
            1.4f));
    private final RepeatingActionManager actionManager = new RepeatingActionManager(
        VoltskiyaPlugin.get()).registerInit(this::initThrow)
        .registerAction(new OneOffAction(CHARGE_UP, this::doChargeUp))
        .registerAction(new OneOffAction(DO_THROW, this::doThrow)).registerFinally(this::quitThrow);
    private int cooldownUpAt = 0;

    public MobFireballThrow(Entity bukkitEntity, Config config) {
        super(bukkitEntity, config);
        random = new ChanceRolling(config.throwChance);
    }

    private boolean isCooldownUp() {
        return getTicksLived() >= cooldownUpAt;
    }

    @Override
    public void tick(int tickSpeed) {
        if (shouldDoAbility() && random.rollXTimes(tickSpeed)) {
            actionManager.start().startAction(CHARGE_UP);
        }
    }

    private boolean shouldDoAbility() {
        if (!isCooldownUp())
            return false;
        @Nullable LivingEntity target = getTarget();
        if (!(target instanceof Player player))
            return false;
        double distanceToTarget = VectorUtils.distance(player.getLocation(), getLocation());
        if (!getBukkitMob().hasLineOfSight(player))
            return false;
        return NumberUtils.betweenDouble(config.minSight, distanceToTarget, config.maxSight);
    }

    private void initThrow() {
        cooldownUpAt = getTicksLived() + config.spellCooldown;
        setIsDoingAction(true);
    }

    private void quitThrow() {
        setIsDoingAction(false);
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

    @Override
    protected void kill() {

    }


}
