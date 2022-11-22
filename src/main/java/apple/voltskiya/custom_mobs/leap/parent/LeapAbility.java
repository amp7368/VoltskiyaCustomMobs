package apple.voltskiya.custom_mobs.leap.parent;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.ability.MMAbility;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

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

    protected Location findTarget() {
        if (!this.isMob())
            return null;
        LivingEntity target = this.getMob().getTarget();
        return target == null ? null : target.getLocation();
    }

    protected abstract List<CreateLeapStage<Config>> leapStages();

    private void nextLeap() {
        stageNormalFinish();
        List<CreateLeapStage<Config>> stages = leapStages();
        if (leapStage >= stages.size()) {
            this.finishAbility();
            return;
        }
        this.targetLocation = this.findTarget();
        this.leapInProgress = stages.get(leapStage).create(mob, config, targetLocation);
        this.leapInProgress.onStart();
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
        if (!isLeapInProgress())
            return;
        this.leapInProgress.tick_();
        VoltskiyaPlugin.get().scheduleSyncDelayedTask(this::tick, 1);
    }

    private boolean isLeapInProgress() {
        if (this.leapInProgress == null)
            return false;
        if (this.leapInProgress.isFinished()) {
            leapStage++;
            nextLeap();
            return this.isLeapInProgress();
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
