package apple.voltskiya.custom_mobs.leap.pounce;

import apple.voltskiya.custom_mobs.leap.parent.config.LeapConfig;
import apple.voltskiya.custom_mobs.leap.parent.config.LeapStageConfig;
import apple.voltskiya.mob_manager.mob.MMSpawned;

public class LeapPounceConfig extends LeapConfig {

    public int stunTime = 60;
    public LeapStageConfig preLeap = new LeapStageConfig();

    public LeapPounceConfig(String prefix, String tag) {
        super(prefix, tag);
    }

    public LeapPounceConfig() {
        super();
    }

    @Override
    public void doSpawn(MMSpawned mob) {
        new LeapPounceAbility<>(mob, this);
    }
}
