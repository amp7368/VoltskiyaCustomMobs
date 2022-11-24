package apple.voltskiya.custom_mobs.leap.basic;

import apple.utilities.database.SaveFileable;
import apple.voltskiya.custom_mobs.leap.parent.config.LeapConfig;
import apple.voltskiya.mob_manager.mob.MMSpawned;

public class LeapBasicConfig extends LeapConfig implements SaveFileable {

    public LeapBasicConfig(String prefix, String tag) {
        super(prefix, tag);
    }

    public LeapBasicConfig() {
        super();
    }

    @Override
    public void doSpawn(MMSpawned mob) {
        new LeapBasicAbility<>(mob, this);
    }
}
