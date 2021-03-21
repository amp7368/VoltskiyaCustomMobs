package apple.voltskiya.custom_mobs.tick;


import org.bukkit.event.entity.CreatureSpawnEvent;

public abstract class SpawnEater extends ConfigManager {
    abstract public void eatEvent(CreatureSpawnEvent event);
}
