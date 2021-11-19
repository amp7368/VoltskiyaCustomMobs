package apple.voltskiya.custom_mobs.mobs.abilities.tick.mancubus.mob;

import apple.utilities.util.NumberUtils;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.mobs.abilities.MobTickPlugin;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.mancubus.config.MancubusConfig;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobToTick;
import org.bukkit.Location;
import org.bukkit.Particle;
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
import voltskiya.apple.utilities.util.particle.ParticleCircle;
import voltskiya.apple.utilities.util.sound.SoundActionImpl;
import voltskiya.apple.utilities.util.sound.SoundManager;

public abstract class MobMancubus<Config extends MancubusConfig> extends MobToTick<Config> {
    private static final String DO_START = "start";
    private static final String DO_BURST1 = "burst_1";
    private static final String DO_BURST2 = "burst_2";
    private static final String DO_BURST3 = "burst_3";

    private static final String SOUND_GROWL = "growl";
    private static final String SOUND_WARNING1 = "warning1";
    private static final String SOUND_WARNING2 = "warning2";
    private static final String SOUND_WARNING3 = "warning3";
    private static final String SOUND_BURST1 = "burst_1";
    private static final String SOUND_BURST2 = "burst_2";
    private static final String SOUND_BURST3 = "burst_3";
    private final SoundManager soundManager = new SoundManager()
            .registerSound(new SoundActionImpl(SOUND_GROWL, Sound.ENTITY_POLAR_BEAR_WARNING, SoundCategory.HOSTILE, 0.5f, 1.7f))
            .registerSound(new SoundActionImpl(SOUND_WARNING1, Sound.ENTITY_ZOMBIE_HURT, SoundCategory.HOSTILE, 0.2f, 1.7f))
            .registerSound(new SoundActionImpl(SOUND_WARNING2, Sound.ENTITY_ZOMBIE_HURT, SoundCategory.HOSTILE, 0.5f, 1.7f))
            .registerSound(new SoundActionImpl(SOUND_WARNING3, Sound.ENTITY_ZOMBIE_HURT, SoundCategory.HOSTILE, 0.8f, 1.7f))
            .registerSound(new SoundActionImpl(SOUND_BURST1, Sound.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 0.6f, 1.5F))
            .registerSound(new SoundActionImpl(SOUND_BURST2, Sound.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 0.5f, 1.5f))
            .registerSound(new SoundActionImpl(SOUND_BURST3, Sound.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 0.45f, 1.6f));
    private final RepeatingActionManager burst;
    ParticleCircle particle = new ParticleCircle(null);
    private int cooldownUpAt = 0;
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
        Location myLocation = getEyeLocation();
        @NotNull Vector direction = target.getEyeLocation().clone().subtract(myLocation).toVector().normalize();
        this.getBukkitMob().teleport(getLocation().setDirection(direction));
    }

    private void doStart() {
        if (isDead()) return;
        this.soundManager.playSound(SOUND_WARNING1, getLocation());
        this.soundManager.playSound(SOUND_GROWL, getLocation());
        angerParticles();
        VoltskiyaPlugin.get().scheduleSyncDelayedTask(() -> this.soundManager.playSound(SOUND_WARNING2, getLocation()), 8);
        VoltskiyaPlugin.get().scheduleSyncDelayedTask(() -> this.soundManager.playSound(SOUND_WARNING3, getLocation()), 20);
        this.burst.startAction(DO_BURST1);
    }

    private void angerParticles() {
        particle.setCenter(getLocation());
        getWorld().spawnParticle(Particle.VILLAGER_ANGRY, getLocation(), 10, 0.3, 0.3, 0.3, 0);
    }

    private void throwParticles() {
        particle.setCenter(getLocation());
        Location[] particles = particle.innerToOuterWithCount(5, 0.6, .2, 1.0);
        particle.particles(particles, (world, location) -> {
            world.spawnParticle(Particle.LAVA, location, 2, 0.6, 1, 0.6);
        });
    }

    private void burst1() {
        if (isDead()) return;
        Location myLocation = getEyeLocation();
        @NotNull Vector direction = target.getEyeLocation().clone().subtract(myLocation).toVector().normalize();
        throwParticles();
        shootFireball(myLocation, direction, config.burst1Angle1);
        shootFireball(myLocation, direction, config.burst1Angle2);
        this.burst.startAction(DO_BURST2);
    }


    private void burst2() {
        if (isDead()) return;
        Location myLocation = getEyeLocation();
        @NotNull Vector direction = target.getEyeLocation().clone().subtract(myLocation).toVector().normalize();
        throwParticles();
        shootFireball(myLocation, direction, config.burst2Angle1);
        shootFireball(myLocation, direction, config.burst2Angle2);
        this.burst.startAction(DO_BURST3);
    }

    private void burst3() {
        if (isDead()) return;
        Location myLocation = getEyeLocation();
        @NotNull Vector direction = target.getEyeLocation().clone().subtract(myLocation).toVector().normalize();
        throwParticles();
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
