package apple.voltskiya.custom_mobs.abilities.nether.fireball.config;

import apple.voltskiya.custom_mobs.abilities.nether.fireball.mob.MobFireballThrow;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.ability.MMAbilityConfig;

public class FireballThrowConfig extends MMAbilityConfig {

    public double minSight = 4d;
    public double maxSight = 100000;
    public double shotSpeed = 1d;
    private final transient String tag;

    public FireballThrowConfig(String tag) {
        this.tag = tag;
    }

    @Override
    public boolean isOnlyMobs() {
        return true;
    }

    @Override
    public String getBriefTag() {
        return this.tag;
    }

    @Override
    public void doSpawn(MMSpawned mob) {
        new MobFireballThrow<>(mob, this);
    }
}
