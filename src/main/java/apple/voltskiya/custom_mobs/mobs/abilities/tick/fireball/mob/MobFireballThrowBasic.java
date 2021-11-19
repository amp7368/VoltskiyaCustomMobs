package apple.voltskiya.custom_mobs.mobs.abilities.tick.fireball.mob;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.fireball.config.FireballThrowConfigBasic;
import org.bukkit.entity.Entity;

public class MobFireballThrowBasic extends MobFireballThrow<FireballThrowConfigBasic> {
    public MobFireballThrowBasic(Entity bukkitEntity, FireballThrowConfigBasic config) {
        super(bukkitEntity, config);
    }
}
