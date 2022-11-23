package apple.voltskiya.custom_mobs.leap.basic;

import apple.utilities.database.SaveFileable;
import apple.voltskiya.custom_mobs.leap.parent.config.LeapConfig;
import apple.voltskiya.mob_manager.mob.MMSpawned;

public class LeapBasicConfig extends LeapConfig implements SaveFileable {

    public String tag;

    public LeapBasicConfig(String tag) {
        super(tag);
        this.tag = tag;
    }

    public LeapBasicConfig() {
    }

    @Override
    public String getBriefTag() {
        return this.tag;
        // use the serialized tag instead
    }

    @Override
    public void doSpawn(MMSpawned mob) {
        new LeapBasicAbility<>(mob, this);
    }

    @Override
    public String getSaveFileName() {
        return extensionJson(tag);
    }
}
