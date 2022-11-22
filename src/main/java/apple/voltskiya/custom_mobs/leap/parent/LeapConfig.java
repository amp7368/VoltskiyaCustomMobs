package apple.voltskiya.custom_mobs.leap.parent;

import apple.voltskiya.mob_manager.mob.ability.MMAbilityConfig;
import apple.voltskiya.mob_manager.mob.ability.activation.Activation;
import apple.voltskiya.mob_manager.mob.ability.activation.ActivationRange;
import java.util.Collection;
import java.util.List;

public abstract class LeapConfig extends MMAbilityConfig {

    private final transient String tag;
    public double gravity = -0.1;
    public double velocity = 0.75;

    public LeapStageConfig leap = new LeapStageConfig();
    public ActivationRange range = new ActivationRange(10);

    public LeapConfig(String tag) {
        this.tag = tag;
    }

    @Override
    public String getExtensionTag() {
        return "leap";
    }

    @Override
    public Collection<Activation> getActivations() {
        return List.of(range);
    }

    @Override
    public String getBriefTag() {
        return this.tag;
    }
}
