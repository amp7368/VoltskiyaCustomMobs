package apple.voltskiya.custom_mobs.abilities.common.reviver.mob;

import apple.mc.utilities.world.vector.VectorUtils;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.abilities.common.reviver.config.ReviverConfigPulse;
import apple.voltskiya.custom_mobs.abilities.common.reviver.dead.DeadRecordedMob;
import apple.voltskiya.custom_mobs.abilities.common.reviver.dead.ReviveDeadManager;
import apple.voltskiya.custom_mobs.util.PlayerClose;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import java.util.List;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Mob;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.utilities.action.ActionMeta;
import voltskiya.apple.utilities.action.ActionReturn;
import voltskiya.apple.utilities.action.ActionToRun;
import voltskiya.apple.utilities.action.OneOffAction;
import voltskiya.apple.utilities.action.RepeatableActionImpl;
import voltskiya.apple.utilities.action.RepeatingActionManager;
import voltskiya.apple.utilities.particle.ParticleCircle;

public class MobReviverPulse extends MobReviver<ReviverConfigPulse> {

    private static final String CHARGE_UP = "charge_up";
    private static final String ANGER = "anger";
    private static final String DO_PULSE = "pulse";
    private final RepeatingActionManager pulse;

    public MobReviverPulse(MMSpawned reviver, ReviverConfigPulse config) {
        super(reviver, config);
        pulse = new RepeatingActionManager(VoltskiyaPlugin.get()).registerInit(this::initPulse)
            .registerAction(
                new RepeatableActionImpl(CHARGE_UP, this::doChargeUp, config.ticksToChargeUp, 1))
            .registerAction(
                new RepeatableActionImpl(DO_PULSE, DoPulse::new, config.ticksForPulse, 1))
            .registerAction(new OneOffAction(ANGER, this::doAnger))
            .registerFinally(this::quitPulse);
    }

    @Override
    protected void startAbility() {
        pulse.start();
    }

    @Override
    public void cleanUp(boolean isDead) {
        pulse.stop();
    }

    private void initPulse() {
        getMob().setAI(false);
        Location location = getLocation();
        location.setPitch(45f);
        getMob().teleport(location);
        pulse.startAction(CHARGE_UP);
    }

    private void quitPulse() {
        getMob().setAI(true);
        pulse.stop();
        this.finishAbility();
    }


    private void doAnger() {
        Mob bukkitMob = getMob();
        @Nullable PlayerClose playerClose = UpdatedPlayerList.getClosestPlayerInGamemode(
            getLocation(), GameMode.SURVIVAL);
        if (playerClose != null
            && VectorUtils.distance(getLocation(), playerClose.getLocation()) < 50) {
            bukkitMob.setTarget(playerClose.player());
        }
        bukkitMob.addPotionEffect(
            new PotionEffect(PotionEffectType.SPEED, config.ticksForAnger, 1, false, true));
        ParticleCircle particleCircle = new ParticleCircle(getLocation().add(0, 1, 0));
        Location[] locations = particleCircle.innerToOuter(1.2, .75, 0, 1);
        particleCircle.particles(locations,
            (world, location) -> world.spawnParticle(Particle.ANGRY_VILLAGER, location, 1));
        getWorld().playSound(getLocation(), Sound.ENTITY_POLAR_BEAR_WARNING, SoundCategory.HOSTILE,
            2, 0.5f);
    }

    private ActionReturn doChargeUp(ActionMeta meta) {
        if (this.mob.isDead())
            return ActionReturn.stop();
        if (wasHit(10)) {
            pulse.startAction(ANGER);
            return ActionReturn.stop();
        }
        if (meta.currentTick() == 1) {
            getWorld().playSound(getLocation(), Sound.BLOCK_BEACON_POWER_SELECT,
                SoundCategory.HOSTILE, 2, 0.1f);
        }
        ParticleCircle particleCircle = new ParticleCircle(getLocation());
        Location[] locations = particleCircle.innerToOuter(1.2, .75, 0, 1);
        particleCircle.particles(locations, Particle.SOUL_FIRE_FLAME);

        if (meta.isLastRun())
            pulse.startAction(DO_PULSE);
        return ActionReturn.go();
    }

    private final class DoPulse implements ActionToRun {

        private final ParticleCircle circle = new ParticleCircle(getLocation());

        @Override
        public ActionReturn run(ActionMeta meta) {
            circle.setCenter(getLocation());
            double density;
            Particle particle;
            density = 2.5;
            if (meta.isLastRun()) {
                density *= 1;
                particle = Particle.SOUL_FIRE_FLAME;
            } else {
                particle = Particle.ENCHANTED_HIT;
            }
            double nowRadius = Math.max(
                ((meta.currentTick() - 1) % config.ticksForPulse) / (double) config.ticksForPulse
                    * config.pulseRadius, 0.5);
            List<DeadRecordedMob> deadInReach = ReviveDeadManager.removeMobsInRadius(
                config.deadTooLong, getLocation(), nowRadius);
            for (DeadRecordedMob dead : deadInReach) {
                doReviveSummon(dead);
            }
            circle.particles(
                circle.innerToOuter(density, nowRadius, Math.max(0, nowRadius - 2), .5), particle);
            circle.particles(circle.hollow(density, nowRadius, .75), particle);
            return ActionReturn.go();
        }
    }
}
