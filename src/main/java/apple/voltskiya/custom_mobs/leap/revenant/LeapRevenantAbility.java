package apple.voltskiya.custom_mobs.leap.revenant;

import apple.voltskiya.custom_mobs.leap.parent.CreateLeapStage;
import apple.voltskiya.custom_mobs.leap.parent.Leap;
import apple.voltskiya.custom_mobs.leap.parent.LeapAbility;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import java.util.List;

public class LeapRevenantAbility<Config extends LeapRevenantConfig> extends LeapAbility<Config> {

    public LeapRevenantAbility(MMSpawned mob, Config config) {
        super(mob, config);
    }

    @Override
    protected List<CreateLeapStage<Config>> leapStages() {
        return List.of(Leap::new);
    }


    @Override
    protected boolean isAbilityBlocking() {
        return false;
    }
}
