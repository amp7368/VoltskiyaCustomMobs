package apple.voltskiya.custom_mobs.abilities.nether.fireball.config;

import apple.voltskiya.custom_mobs.abilities.nether.fireball.mob.MobFireballThrow;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.ability.MMAbilityConfig;
import apple.voltskiya.mob_manager.mob.ability.activation.Activation;
import apple.voltskiya.mob_manager.mob.ability.activation.ActivationRange;
import java.util.Collection;
import java.util.List;

public class FireballThrowConfig extends MMAbilityConfig {

    public double shotSpeed = 1d;
    public ActivationRange range = new ActivationRange(4, -1);
    public transient String tag;

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

    @Override
    public Collection<Activation> getActivations() {
        return List.of(range);
    }
}
