package apple.voltskiya.custom_mobs.abilities.common.grenade.bomb;

import apple.voltskiya.custom_mobs.abilities.common.grenade.GrenadeConfig;
import apple.voltskiya.mob_manager.mob.MMSpawned;

public class ThrowBombConfig extends GrenadeConfig {

    public double explosionRadius = 13f;
    public float explosionPower = 2f;

    @Override
    protected String getGrenadeTag() {
        return "bomb";
    }

    @Override
    public void doSpawn(MMSpawned mmSpawned) {

    }
}
