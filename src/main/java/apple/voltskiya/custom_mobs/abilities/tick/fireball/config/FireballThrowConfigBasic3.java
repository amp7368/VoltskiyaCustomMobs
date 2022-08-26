package apple.voltskiya.custom_mobs.abilities.tick.fireball.config;

import apple.voltskiya.custom_mobs.abilities.tick.parent.MobToTick;
import apple.voltskiya.custom_mobs.abilities.tick.fireball.mob.MobFireballThrow;
import org.bukkit.entity.Entity;

public class FireballThrowConfigBasic3 extends FireballThrowConfig {
    @Override
    public String getTag() {
        return "fireball_throw3";
    }

    @Override
    public MobToTick<?> createMob(Entity entity) {
        return new MobFireballThrow<>(entity, this);
    }
}
