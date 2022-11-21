package apple.voltskiya.custom_mobs.abilities.nether.warper;

import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.ability.MMAbilityConfig;
import apple.voltskiya.mob_manager.mob.ability.activation.Activation;
import apple.voltskiya.mob_manager.mob.ability.activation.ActivationRange;
import java.util.Collection;
import java.util.List;

public class WarperConfig extends MMAbilityConfig {

    public int particles = 3;
    public ActivationRange range = new ActivationRange(7);
    private transient String tag;

    public WarperConfig(String tag) {
        this.tag = tag;
    }

    public WarperConfig() {
    }

    @Override
    public void doSpawn(MMSpawned mob) {
        new MobWarper(mob, this);
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
