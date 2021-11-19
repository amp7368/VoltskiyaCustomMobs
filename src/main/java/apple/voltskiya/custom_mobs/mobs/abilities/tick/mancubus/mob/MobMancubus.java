package apple.voltskiya.custom_mobs.mobs.abilities.tick.mancubus.mob;

import apple.utilities.util.NumberUtils;
import apple.voltskiya.custom_mobs.mobs.abilities.MobTickPlugin;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.mancubus.config.MancubusConfig;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobToTick;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.utilities.util.DistanceUtils;
import voltskiya.apple.utilities.util.VectorUtils;
import voltskiya.apple.utilities.util.action.OneOffAction;
import voltskiya.apple.utilities.util.action.RepeatingActionManager;
import voltskiya.apple.utilities.util.action.ScheduledAction;
import voltskiya.apple.utilities.util.constants.TagConstants;
import voltskiya.apple.utilities.util.sound.SoundActionImpl;
import voltskiya.apple.utilities.util.sound.SoundManager;

public abstract class MobMancubus<Config extends MancubusConfig> extends MobToTick<Config> {
    private static final String DO_START = "start";
    private static final String DO_BURST1 = "burst_1";
    private static final String DO_BURST2 = "burst_2";
    private static final String DO_BURST3 = "burst_3";

    private static final String SOUND_WARNING = "warning";
    private static final String SOUND_BURST1 = "burst_1";
    private static final String SOUND_BURST2 = "burst_2";
    private static final String SOUND_BURST3 = "burst_3";

    private final RepeatingActionManager burst;
    private final SoundManager soundManager = new SoundManager()
            .registerSound(new SoundActionImpl(SOUND_WARNING, Sound.ENTITY_HUSK_HURT, SoundCategory.HOSTILE, 0.7f, 1.4f))
            .registerSound(new SoundActionImpl(SOUND_BURST1, Sound.ENTITY_HUSK_HURT, SoundCategory.HOSTILE, 0.7f, 1.4f))
            .registerSound(new SoundActionImpl(SOUND_BURST2, Sound.ENTITY_HUSK_HURT, SoundCategory.HOSTILE, 0.7f, 1.4f))
            .registerSound(new SoundActionImpl(SOUND_BURST3, Sound.ENTITY_HUSK_HURT, SoundCategory.HOSTILE, 0.7f, 1.4f));
    private int cooldownUpAt;
    private LivingEntity target = null;

    public MobMancubus(Entity bukkitEntity, Config config) {
        super(bukkitEntity, config);
        this.burst = MobTickPlugin.get().createRepeatingAction()
                .registerInit(this::initBurst)
                .registerAction(new OneOffAction(DO_START, this::doStart))
                .registerAction(new ScheduledAction(DO_BURST1, this::burst1, config.burstDelay1 + config.initialDelay))
                .registerAction(new ScheduledAction(DO_BURST2, this::burst2, config.burstDelay2))
                .registerAction(new ScheduledAction(DO_BURST3, this::burst3, config.burstDelay3))
                .registerFinally(this::quitBurst);
    }


    @Override
    public void tick(int tickSpeed) {
        if (shouldDoAbility()) {
            this.target = getTarget();
            if (this.target != null)
                burst.startActionAndStart(DO_START);
        }
    }

    private boolean shouldDoAbility() {
        if (!isCooldownUp()) return false;
        @Nullable LivingEntity target = getTarget();
        if (target == null) return false;
        double distanceToTarget = DistanceUtils.distance(target.getLocation(), getLocation());
        if (!getBukkitMob().hasLineOfSight(target)) return false;
        return NumberUtils.betweenDouble(config.minSight, distanceToTarget, config.maxSight);
    }

    private boolean isCooldownUp() {
        return getTicksLived() >= cooldownUpAt;
    }

    @Override
    protected void kill() {

    }


    private void initBurst() {
        getBukkitMob().setAI(false);
        setIsDoingAction(true);
    }

    private void doStart() {
        this.soundManager.playSound(SOUND_WARNING, getLocation());
        this.burst.startAction(DO_BURST1);
    }

    private void burst1() {
        Location myLocation = getEyeLocation();
        @NotNull Vector direction = target.getEyeLocation().clone().subtract(myLocation).toVector().normalize();
        shootFireball(myLocation, direction, config.burst1Angle1);
        shootFireball(myLocation, direction, config.burst1Angle2);
        this.burst.startAction(DO_BURST2);
    }


    private void burst2() {
        Location myLocation = getEyeLocation();
        @NotNull Vector direction = target.getEyeLocation().clone().subtract(myLocation).toVector().normalize();
        shootFireball(myLocation, direction, config.burst2Angle1);
        shootFireball(myLocation, direction, config.burst2Angle2);
        this.burst.startAction(DO_BURST3);
    }

    private void burst3() {
        Location myLocation = getEyeLocation();
        @NotNull Vector direction = target.getEyeLocation().clone().subtract(myLocation).toVector().normalize();
        shootFireball(myLocation, direction, config.burst3Angle1);
        shootFireball(myLocation, direction, config.burst3Angle2);
    }

    protected void shootFireball(Location location, @NotNull Vector direction, double rotation) {
        direction = VectorUtils.rotateVector(direction.getX(), direction.getZ(), direction.getY(), Math.toRadians(rotation));
        @NotNull Entity entity = location.getWorld().spawnEntity(location, EntityType.FIREBALL, CreatureSpawnEvent.SpawnReason.CUSTOM);
        if (entity instanceof Fireball fireball) {
            fireball.setVelocity(direction.multiply(config.shotSpeed));
            fireball.setDirection(direction);
            fireball.addScoreboardTag(TagConstants.noCollideFireballThrow);
        }
    }

    private void quitBurst() {
        cooldownUpAt = getTicksLived() + config.spellCooldown;
        getBukkitMob().setAI(true);
        setIsDoingAction(false);
        this.target = null;
    }
}
