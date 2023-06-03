package apple.voltskiya.custom_mobs.abilities.common.grenade.player;

import apple.voltskiya.custom_mobs.abilities.common.grenade.flashbang.FlashbangConfig;

public class PlayerGrenadeConfig {

    private static PlayerGrenadeConfig instance;
    public final FlashbangConfig flashbang = new FlashbangConfig();

    public PlayerGrenadeConfig() {
        instance = this;
    }

    public static PlayerGrenadeConfig get() {
        return instance;
    }

}
