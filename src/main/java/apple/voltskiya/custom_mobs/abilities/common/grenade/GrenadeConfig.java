package apple.voltskiya.custom_mobs.abilities.common.grenade;

import apple.voltskiya.mob_manager.mob.ability.MMAbilityConfig;

public abstract class GrenadeConfig extends MMAbilityConfig {

    @Override
    public String getBriefTag() {
        return "grenade." + getGrenadeTag();
    }

    protected abstract String getGrenadeTag();
}
