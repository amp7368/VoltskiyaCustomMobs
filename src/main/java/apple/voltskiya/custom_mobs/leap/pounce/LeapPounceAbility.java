package apple.voltskiya.custom_mobs.leap.pounce;

import apple.voltskiya.custom_mobs.leap.parent.CreateLeapStage;
import apple.voltskiya.custom_mobs.leap.parent.LeapAbility;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import java.util.List;
import java.util.UUID;
import org.bukkit.attribute.AttributeModifier;

public class LeapPounceAbility<Config extends LeapPounceConfig> extends LeapAbility<Config> {

    public static final AttributeModifier NO_MOVE_ATTRIBUTE = new AttributeModifier(UUID.randomUUID(), "no_move", -100,
        AttributeModifier.Operation.ADD_SCALAR);

    public LeapPounceAbility(MMSpawned mob, Config config) {
        super(mob, config);
    }


    @Override
    protected List<CreateLeapStage<Config>> leapStages() {
        return List.of(PreLeapPounce::new, LeapPounce::new);
    }
}
