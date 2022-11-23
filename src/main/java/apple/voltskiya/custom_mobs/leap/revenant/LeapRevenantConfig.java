package apple.voltskiya.custom_mobs.leap.revenant;

import apple.voltskiya.custom_mobs.leap.parent.config.LeapConfig;
import apple.voltskiya.mob_manager.mob.MMSpawned;

public class LeapRevenantConfig extends LeapConfig {


    public LeapRevenantConfig(String tag) {
        super(tag);
    }

    public LeapRevenantConfig() {
    }

    @Override
    public void doSpawn(MMSpawned mob) {
        new LeapRevenantAbility<>(mob, this);
    }
}
