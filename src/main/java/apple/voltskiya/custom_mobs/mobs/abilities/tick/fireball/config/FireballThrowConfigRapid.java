package apple.voltskiya.custom_mobs.mobs.abilities.tick.fireball.config;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.fireball.mob.MobFireballThrow;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobToTick;
import org.bukkit.entity.Entity;

public class FireballThrowConfigRapid extends FireballThrowConfig {
    @Override
    public String getTag() {
        return "fireball_rapid";
    }

    @Override
    public MobToTick<?> createMob(Entity entity) {
        return new MobFireballThrow<>(entity, this);
    }
}