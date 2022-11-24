package apple.voltskiya.custom_mobs.leap.parent;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.leap.parent.config.LeapConfig;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.ability.MMAbility;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public abstract class LeapAbility<Config extends LeapConfig> extends MMAbility<Config> {

    private LeapStage<Config> leapInProgress = null;

    private int leapStage = 0;
    private Location targetLocation;

    public LeapAbility(MMSpawned mob, Config config) {
        super(mob, config);
    }

    @Override
    protected void startAbility() {
        this.nextLeap();
        this.tick();
    }

    @Override
    protected boolean canStartAbility() {
        if (!this.getEntity().isOnGround())
            return false;
        this.targetLocation = this.findTarget();
        return this.targetLocation != null && this.config.leap.math().estimateIsInRange(this.targetLocation, this.getLocation());
    }

    protected Location findTarget() {
        return this.config.targeting.findTarget(this.mob, this.config);
    }

    protected abstract List<CreateLeapStage<Config>> leapStages();

    private void nextLeap() {
        stageNormalFinish();
        List<CreateLeapStage<Config>> stages = leapStages();
        if (leapStage >= stages.size()) {
            this.finishAbility();
            return;
        }
        onStageChange();
        if (this.leapInProgress != null)
            this.targetLocation = this.leapInProgress.targetLocation;
        if (this.targetLocation == null) {
            this.finishAbility();
            return;
        }
        this.leapInProgress = stages.get(leapStage).create(mob, config, targetLocation);
        this.leapInProgress.onStart();
    }

    protected void onStageChange() {
    }

    private void stageNormalFinish() {
        if (this.leapInProgress != null) {
            this.leapInProgress.onNormalFinish();
            this.leapInProgress.onAnyFinish();
        }
    }

    private void stageCancel() {
        if (this.leapInProgress != null) {
            this.leapInProgress.onCancel();
            this.leapInProgress.onAnyFinish();
        }
    }


    protected void tick() {
        if (!leapInProgressCheck())
            return;
        this.leapInProgress.tick_();
        VoltskiyaPlugin.get().scheduleSyncDelayedTask(this::tick, 1);
    }

    private boolean leapInProgressCheck() {
        if (this.leapInProgress == null)
            return false;
        if (this.leapInProgress.isFinished()) {
            leapStage++;
            nextLeap();
            return this.leapInProgressCheck();
        }
        return true;
    }


    @Override
    protected void onFinishAbility() {
        this.leapStage = 0;
        this.leapInProgress = null;
    }

    @Override
    public void onDamage(EntityDamageEvent event) {
        if (this.leapInProgress != null && event.getCause() == DamageCause.FALL) {
            event.setCancelled(true);
            return;
        }
        this.stageCancel();
        this.finishAbility();
    }

    @Override
    public void cleanUp(boolean isDead) {
        this.leapStage = 0;
        this.leapInProgress = null;
        this.targetLocation = null;
        this.stageCancel();
    }
}
