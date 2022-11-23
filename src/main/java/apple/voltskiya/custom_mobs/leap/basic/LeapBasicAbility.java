package apple.voltskiya.custom_mobs.leap.basic;

import apple.voltskiya.custom_mobs.leap.parent.CreateLeapStage;
import apple.voltskiya.custom_mobs.leap.parent.Leap;
import apple.voltskiya.custom_mobs.leap.parent.LeapAbility;
import apple.voltskiya.custom_mobs.leap.parent.config.LeapConfig;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import java.util.List;

public class LeapBasicAbility<Config extends LeapConfig> extends LeapAbility<Config> {

    public LeapBasicAbility(MMSpawned mob, Config config) {
        super(mob, config);
    }

    @Override
    protected List<CreateLeapStage<Config>> leapStages() {
        return List.of(Leap::new);
    }
}
