package apple.voltskiya.custom_mobs.leap.pounce;

import apple.voltskiya.custom_mobs.leap.parent.LeapConfig;
import apple.voltskiya.custom_mobs.leap.parent.LeapStageConfig;
import apple.voltskiya.mob_manager.mob.MMSpawned;

public class PounceConfig extends LeapConfig {

    public int stunTime = 60;
    public LeapStageConfig preLeap = new LeapStageConfig();

    public PounceConfig(String tag) {
        super(tag);
    }

    @Override
    public void doSpawn(MMSpawned mob) {
        new LeapPounceAbility<>(mob, this);
    }
}
