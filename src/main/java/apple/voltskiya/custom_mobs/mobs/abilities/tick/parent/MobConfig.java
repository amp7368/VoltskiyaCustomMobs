package apple.voltskiya.custom_mobs.mobs.abilities.tick.parent;

import org.bukkit.entity.Entity;

public interface MobConfig {
    String getTag();

    MobToTick<?> createMob(Entity entity);
}
