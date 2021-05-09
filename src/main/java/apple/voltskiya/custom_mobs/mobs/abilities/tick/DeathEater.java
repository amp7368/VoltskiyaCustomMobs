package apple.voltskiya.custom_mobs.mobs.abilities.tick;

import apple.voltskiya.custom_mobs.ConfigManager;
import org.bukkit.event.entity.EntityDeathEvent;

public abstract class DeathEater extends ConfigManager {
    abstract public void eatEvent(EntityDeathEvent event);
}
