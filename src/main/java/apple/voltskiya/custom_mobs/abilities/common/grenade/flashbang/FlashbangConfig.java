package apple.voltskiya.custom_mobs.abilities.common.grenade.flashbang;

import apple.voltskiya.custom_mobs.abilities.common.grenade.GrenadeConfig;
import apple.voltskiya.mob_manager.mob.MMSpawned;

public class FlashbangConfig extends GrenadeConfig {

    public double explosionRadius = 20;
    public long maxBlindnessDuration = 6 * 20;

    @Override
    public void doSpawn(MMSpawned mmSpawned) {
        new MobFlashbang(mmSpawned, this);
    }

    @Override
    protected String getGrenadeTag() {
        return "flashbang";
    }

    public float velocity() {
        return 5f;
    }
}
