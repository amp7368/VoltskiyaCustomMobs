package apple.voltskiya.custom_mobs.abilities.tick.parent;

import org.bukkit.entity.Entity;

public interface MobTickerConfig {
    String getTag();

    MobToTick<?> createMob(Entity entity);
}
