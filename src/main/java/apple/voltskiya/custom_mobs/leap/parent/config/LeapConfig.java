package apple.voltskiya.custom_mobs.leap.parent.config;

import apple.utilities.database.SaveFileable;
import apple.voltskiya.custom_mobs.leap.parent.targeting.TargetingConfig;
import apple.voltskiya.custom_mobs.leap.parent.targeting.TargetingConfigFollowing;
import apple.voltskiya.mob_manager.mob.ability.MMAbilityConfig;

public abstract class LeapConfig extends MMAbilityConfig implements SaveFileable {

    public String tag;
    public String prefix;
    public TargetingConfig targeting = new TargetingConfigFollowing();
    public LeapMoveConfig leap = new LeapMoveConfig();
    public LeapStageConfig leap_stage = new LeapStageConfig();

    public LeapConfig(String prefix, String tag) {
        this.tag = tag;
        this.prefix = prefix;
    }

    public LeapConfig() {
    }

    @Override
    public String getExtensionTag() {
        return "leap";
    }

    @Override
    public String getBriefTag() {
        if (this.prefix.isEmpty())
            return this.tag;
        return this.prefix + "." + this.tag;
    }

    @Override
    public String getSaveFileName() {
        return extensionJson(this.tag);
    }
}
