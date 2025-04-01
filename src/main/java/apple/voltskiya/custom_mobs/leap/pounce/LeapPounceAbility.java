package apple.voltskiya.custom_mobs.leap.pounce;

import apple.voltskiya.custom_mobs.leap.parent.CreateLeapStage;
import apple.voltskiya.custom_mobs.leap.parent.LeapAbility;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import java.util.List;

public class LeapPounceAbility<Config extends LeapPounceConfig> extends LeapAbility<Config> {

    public LeapPounceAbility(MMSpawned mob, Config config) {
        super(mob, config);
    }


    @Override
    protected List<CreateLeapStage<Config>> leapStages() {
        return List.of(PreLeapPounce::new, LeapPounce::new);
    }
}
