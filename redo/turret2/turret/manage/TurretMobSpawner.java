package apple.voltskiya.custom_mobs.turret.manage;

import apple.voltskiya.custom_mobs.turret.base.TurretConfig;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class TurretMobSpawner implements Listener {

    private static final Map<String, TurretConfig> configs = new HashMap<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMobSpawn(CreatureSpawnEvent event) {
        for (String tag : event.getEntity().getScoreboardTags()) {
            TurretConfig config = configs.get(tag);
            if (config == null) continue;
            config.spawnNew(event.getLocation());
            event.setCancelled(true);
            break;
        }
    }
}
