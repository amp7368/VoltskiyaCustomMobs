package apple.voltskiya.custom_mobs.leap.parent;

import apple.voltskiya.mob_manager.mob.HasMMSpawnedUtility;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import org.bukkit.Location;

public abstract class LeapStage<Config extends LeapConfig> implements HasMMSpawnedUtility {

    protected final MMSpawned mob;
    protected final LeapStageConfig config;
    protected final Config fullConfig;
    protected Location targetLocation;
    protected int tick = 0;
    private boolean cancel = false;

    public LeapStage(MMSpawned mob, Config fullConfig, LeapStageConfig config, Location targetLocation) {
        this.mob = mob;
        this.fullConfig = fullConfig;
        this.config = config;
        this.targetLocation = targetLocation;
    }

    @Override
    public MMSpawned getMMSpawned() {
        return this.mob;
    }

    public void tick_() {
        this.tick();
        tick++;
    }

    protected abstract void tick();

    protected boolean isFinished() {
        return tick >= config.duration || this.cancel;
    }

    public void onAnyFinish() {
    }

    public void onNormalFinish() {
    }

    public void onCancel() {
    }

    public void onStart() {
    }

    protected void cancel() {
        this.cancel = true;
    }
}
