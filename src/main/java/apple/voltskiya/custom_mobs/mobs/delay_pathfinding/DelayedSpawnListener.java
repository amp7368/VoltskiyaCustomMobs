package apple.voltskiya.custom_mobs.mobs.delay_pathfinding;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.mobs.delay_pathfinding.patrol.PatrolManager;
import apple.voltskiya.custom_mobs.mobs.nms.parent.register.RegisteredEntityEater;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.HashMap;
import java.util.Map;

public class DelayedSpawnListener implements Listener {
    private final Map<String, RegisteredEntityEater> spawnEaters = new HashMap<>();

    public DelayedSpawnListener() {
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
        spawnEaters.put(PatrolManager.PATROL_TAG, new PatrolManager());
        for (RegisteredEntityEater spawnEater : spawnEaters.values()) {
            spawnEater.registerInDB();
            spawnEater.eatMobs();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onSpawn(CreatureSpawnEvent event) {
        for (String tag : event.getEntity().getScoreboardTags()) {
            String[] tagSplit = tag.split("\\.");
            RegisteredEntityEater eater = spawnEaters.get(tagSplit[0]);
            if (eater != null) eater.eatAndRegisterEvent(event);
        }
    }
}
