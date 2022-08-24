package apple.voltskiya.custom_mobs.mobs.abilities.tick.fireball.config;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.fireball.mob.MobFireballThrowBasic;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobToTick;
import org.bukkit.entity.Entity;

public class FireballThrowConfigBasic extends FireballThrowConfig {
    @Override
    public String getTag() {
        return "fireball_throw";
    }

    @Override
    public MobToTick<?> createMob(Entity entity) {
        return new MobFireballThrowBasic(entity, this);
    }
}
