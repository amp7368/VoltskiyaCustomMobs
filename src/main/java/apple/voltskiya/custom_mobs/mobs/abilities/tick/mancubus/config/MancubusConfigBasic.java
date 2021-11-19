package apple.voltskiya.custom_mobs.mobs.abilities.tick.mancubus.config;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.mancubus.mob.MobMancubusBasic;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobToTick;
import org.bukkit.entity.Entity;

public class MancubusConfigBasic extends MancubusConfig {
    @Override
    public String getTag() {
        return "mancubus";
    }

    @Override
    public MobToTick<?> createMob(Entity entity) {
        return new MobMancubusBasic(entity, this);
    }
}
