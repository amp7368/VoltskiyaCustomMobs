package apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.mob;

import apple.voltskiya.custom_mobs.mobs.abilities.MobTickPlugin;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.config.ReviverConfigPulse;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.dead.DeadRecordedMob;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.dead.ReviveDeadManager;
import apple.voltskiya.custom_mobs.util.PlayerClose;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.utilities.util.DistanceUtils;
import voltskiya.apple.utilities.util.action.ActionToRun;
import voltskiya.apple.utilities.util.action.OneOffAction;
import voltskiya.apple.utilities.util.action.RepeatableActionImpl;
import voltskiya.apple.utilities.util.action.RepeatingActionManager;
import voltskiya.apple.utilities.util.particle.ParticleCircle;

import java.util.List;

public class MobReviverPulse extends MobReviver<ReviverConfigPulse> {
    private static final String CHARGE_UP = "charge_up";
    private static final String ANGER = "anger";
    private static final String DO_PULSE = "pulse";
    private final RepeatingActionManager pulse;
    private int cooldownUpAt;

    public MobReviverPulse(Entity reviver, ReviverConfigPulse config) {
        super(reviver, config);
        this.cooldownUpAt = getTicksLived() + config.spellCooldown;
        pulse = MobTickPlugin.get().createRepeatingAction()
                .registerInit(this::initPulse)
                .registerAction(new RepeatableActionImpl(CHARGE_UP, this::doChargeUp, config.ticksToChargeUp, 1))
                .registerAction(new RepeatableActionImpl(DO_PULSE, DoPulse::new, config.ticksForPulse, 1))
                .registerAction(new OneOffAction(ANGER, this::doAnger))
                .registerFinally(this::quitPulse);
    }

    @Override
    public void tick(int tickSpeed) {
        if (isCooldownUp()) {
            doAbility();
        }
    }

    @Override
    protected void doAbility() {
        pulse.start();
    }

    private void initPulse() {
        getBukkitMob().setAI(false);
        Location location = getLocation();
        location.setPitch(45f);
        getBukkitMob().teleport(location);
        setIsDoingAction(true);
        pulse.startAction(CHARGE_UP);
    }

    private void quitPulse() {
        getBukkitMob().setAI(true);
        setIsDoingAction(false);
        cooldownUpAt = getTicksLived() + config.spellCooldown;
    }


    private void doAnger() {
        Mob bukkitMob = getBukkitMob();
        @Nullable PlayerClose playerClose = UpdatedPlayerList.getClosestPlayerInGamemode(getLocation(), GameMode.SURVIVAL);
        if (playerClose != null && DistanceUtils.distance(getLocation(), playerClose.getLocation()) < 50) {
            bukkitMob.setTarget(playerClose.player());
        }
        bukkitMob.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, config.ticksForAnger, 1, false, true));
        ParticleCircle particleCircle = new ParticleCircle(getLocation().add(0, 1, 0));
        Location[] locations = particleCircle.innerToOuter(1.2, .75, 0, 1);
        particleCircle.particles(locations, (world, location) -> world.spawnParticle(Particle.VILLAGER_ANGRY, location, 1));
        getWorld().playSound(getLocation(), Sound.ENTITY_POLAR_BEAR_WARNING, SoundCategory.HOSTILE, 2, 0.5f);
    }

    private boolean doChargeUp(int currentTick, boolean isLastRun) {
        if (wasHit(10)) {
            pulse.startAction(ANGER);
            return false;
        }
        if (currentTick == 1) {
            getWorld().playSound(getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.HOSTILE, 2, 0.1f);
        }
        ParticleCircle particleCircle = new ParticleCircle(getLocation());
        Location[] locations = particleCircle.innerToOuter(1.2, .75, 0, 1);
        particleCircle.particles(locations, Particle.SOUL_FIRE_FLAME);

        if (isLastRun) pulse.startAction(DO_PULSE);
        return true;
    }


    private boolean isCooldownUp() {
        return getTicksLived() >= cooldownUpAt;
    }

    private final class DoPulse implements ActionToRun {
        private final ParticleCircle circle = new ParticleCircle(getLocation());

        @Override
        public boolean run(int currentTick, boolean isLastTick) {
            circle.setCenter(getLocation());
            double density;
            Particle particle;
            density = 2.5;
            if (isLastTick) {
                density *= 1;
                particle = Particle.SOUL_FIRE_FLAME;
            } else {
                particle = Particle.CRIT_MAGIC;
            }
            double nowRadius = Math.max(((currentTick - 1) % config.ticksForPulse) / (double) config.ticksForPulse * config.pulseRadius, 0.5);
            List<DeadRecordedMob> deadInReach = ReviveDeadManager.removeMobsInRadius(config.deadTooLong, getLocation(), nowRadius);
            for (DeadRecordedMob dead : deadInReach) {
                doReviveSummon(dead);
            }
            circle.particles(circle.innerToOuter(density, nowRadius, Math.max(0, nowRadius - 2), .5), particle);
            circle.particles(circle.hollow(density, nowRadius, .75), particle);
            return true;
        }
    }
}
