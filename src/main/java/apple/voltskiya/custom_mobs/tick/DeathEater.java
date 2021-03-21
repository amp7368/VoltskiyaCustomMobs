package apple.voltskiya.custom_mobs.tick;

import org.bukkit.event.entity.EntityDeathEvent;

public abstract class DeathEater extends ConfigManager{
    abstract public void eatEvent(EntityDeathEvent event);
}
