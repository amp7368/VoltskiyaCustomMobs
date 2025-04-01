package apple.voltskiya.custom_mobs.abilities.nether.mancubus;

import apple.mc.utilities.world.vector.VectorUtils;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.ability.MMAbility;
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
import voltskiya.apple.utilities.action.OneOffAction;
import voltskiya.apple.utilities.action.RepeatingActionManager;
import voltskiya.apple.utilities.action.ScheduledAction;
import voltskiya.apple.utilities.minecraft.TagConstants;
import voltskiya.apple.utilities.particle.ParticleCircle;
import voltskiya.apple.utilities.sound.SoundActionImpl;
import voltskiya.apple.utilities.sound.SoundManager;

public class MobMancubus<Config extends MancubusConfig> extends MMAbility<Config> {

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
    private final SoundManager soundManager = new SoundManager().registerSound(
        new SoundActionImpl(SOUND_GROWL, Sound.ENTITY_POLAR_BEAR_WARNING, SoundCategory.HOSTILE,
            0.5f, 1.7f)).registerSound(
        new SoundActionImpl(SOUND_WARNING1, Sound.ENTITY_ZOMBIE_HURT, SoundCategory.HOSTILE, 0.2f,
            1.7f)).registerSound(
        new SoundActionImpl(SOUND_WARNING2, Sound.ENTITY_ZOMBIE_HURT, SoundCategory.HOSTILE, 0.5f,
            1.7f)).registerSound(
        new SoundActionImpl(SOUND_WARNING3, Sound.ENTITY_ZOMBIE_HURT, SoundCategory.HOSTILE, 0.8f,
            1.7f)).registerSound(
        new SoundActionImpl(SOUND_BURST1, Sound.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 0.6f,
            1.5F)).registerSound(
        new SoundActionImpl(SOUND_BURST2, Sound.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 0.5f,
            1.5f)).registerSound(
        new SoundActionImpl(SOUND_BURST3, Sound.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 0.45f,
            1.6f));
    private final RepeatingActionManager burst;
    ParticleCircle particle = new ParticleCircle(null);
    private LivingEntity target = null;

    public MobMancubus(MMSpawned mob, Config config) {
        super(mob, config);
        this.burst = new RepeatingActionManager(VoltskiyaPlugin.get()).registerInit(this::initBurst)
            .registerAction(new OneOffAction(DO_START, this::doStart)).registerAction(
                new ScheduledAction(DO_BURST1, this::burst1,
                    config.burstDelay1 + config.initialDelay))
            .registerAction(new ScheduledAction(DO_BURST2, this::burst2, config.burstDelay2))
            .registerAction(new ScheduledAction(DO_BURST3, this::burst3, config.burstDelay3))
            .registerFinally(this::quitBurst);
    }

    @Override
    protected boolean canStartAbility() {
        return hasTarget();
    }

    @Override
    protected void startAbility() {
        burst.startActionAndStart(DO_START);
    }

    @Override
    public void cleanUp(boolean isDead) {
        this.quitBurst();
    }


    private void initBurst() {
        getMob().setAI(false);
        Location myLocation = getEyeLocation();
        @NotNull Vector direction = getDirectionToTarget(myLocation);
        this.getMob().teleport(getLocation().setDirection(direction));
    }

    private void doStart() {
        this.soundManager.playSound(SOUND_WARNING1, getLocation());
        this.soundManager.playSound(SOUND_GROWL, getLocation());
        angerParticles();
        VoltskiyaPlugin.get().scheduleSyncDelayedTask(
            () -> this.soundManager.playSound(SOUND_WARNING2, getLocation()), 8);
        VoltskiyaPlugin.get().scheduleSyncDelayedTask(
            () -> this.soundManager.playSound(SOUND_WARNING3, getLocation()), 20);
        this.burst.startAction(DO_BURST1);
    }

    private void angerParticles() {
        particle.setCenter(getLocation());
        getWorld().spawnParticle(Particle.ANGRY_VILLAGER, getLocation(), 10, 0.3, 0.3, 0.3, 0);
    }

    private void throwParticles() {
        particle.setCenter(getLocation());
        Location[] particles = particle.innerToOuterWithCount(5, 0.6, .2, 1.0);
        particle.particles(particles, (world, location) -> {
            world.spawnParticle(Particle.LAVA, location, 2, 0.6, 1, 0.6);
        });
    }

    private void burst1() {
        if (isDead())
            return;
        Location myLocation = getEyeLocation();
        @NotNull Vector direction = getDirectionToTarget(myLocation);
        throwParticles();
        shootFireball(myLocation, direction, config.burst1Angle1);
        shootFireball(myLocation, direction, config.burst1Angle2);
        this.burst.startAction(DO_BURST2);
    }


    private void burst2() {
        if (isDead())
            return;
        Location myLocation = getEyeLocation();
        @NotNull Vector direction = getDirectionToTarget(myLocation);
        throwParticles();
        shootFireball(myLocation, direction, config.burst2Angle1);
        shootFireball(myLocation, direction, config.burst2Angle2);
        this.burst.startAction(DO_BURST3);
    }

    @NotNull
    private Vector getDirectionToTarget(Location myLocation) {
        return target.getEyeLocation().clone().subtract(myLocation).toVector().normalize();
    }

    private void burst3() {
        if (isDead())
            return;
        Location myLocation = getEyeLocation();
        @NotNull Vector direction = getDirectionToTarget(myLocation);
        throwParticles();
        shootFireball(myLocation, direction, config.burst3Angle1);
        shootFireball(myLocation, direction, config.burst3Angle2);
    }

    protected void shootFireball(Location location, @NotNull Vector direction, double rotation) {
        direction = VectorUtils.rotateVector(direction.getX(), direction.getZ(), direction.getY(),
            Math.toRadians(rotation));
        @NotNull Entity entity = location.getWorld()
            .spawnEntity(location, EntityType.FIREBALL, CreatureSpawnEvent.SpawnReason.CUSTOM);
        if (entity instanceof Fireball fireball) {
            fireball.setVelocity(direction.multiply(config.shotSpeed));
            fireball.setDirection(direction);
            fireball.addScoreboardTag(TagConstants.NO_COLLIDE_FIREBALL_THROW);
        }
    }

    private void quitBurst() {
        this.burst.stop();
        this.finishAbility();
        getMob().setAI(true);
        this.target = null;
    }

}
