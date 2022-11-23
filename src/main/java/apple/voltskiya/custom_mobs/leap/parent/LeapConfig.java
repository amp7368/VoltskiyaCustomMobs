package apple.voltskiya.custom_mobs.leap.parent;

import apple.voltskiya.mob_manager.mob.ability.MMAbilityConfig;

public abstract class LeapConfig extends MMAbilityConfig {

    private transient String tag;
    public LeapMoveConfig leap = new LeapMoveConfig();
    public LeapStageConfig leap_stage = new LeapStageConfig();

    public LeapConfig(String tag) {
        this.tag = tag;
    }
    public LeapConfig() {
    }

    @Override
    public String getExtensionTag() {
        return "leap";
    }

    @Override
    public String getBriefTag() {
        return this.tag;
    }
}
