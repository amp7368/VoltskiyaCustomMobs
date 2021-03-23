package apple.voltskiya.custom_mobs.mob_tick.tick;


import apple.voltskiya.custom_mobs.ConfigManager;
import org.bukkit.event.entity.CreatureSpawnEvent;

public abstract class SpawnEater extends ConfigManager {
    abstract public void eatEvent(CreatureSpawnEvent event);
}
