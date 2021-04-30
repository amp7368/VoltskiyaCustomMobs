package apple.voltskiya.custom_mobs.leaps;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.leaps.config.LeapConfigManager;
import apple.voltskiya.custom_mobs.leaps.config.LeapPreConfig;
import apple.voltskiya.custom_mobs.leaps.misc.LeapSpecificMisc;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.HashMap;
import java.util.Map;

public class LeapSpawnListener implements Listener {
    private final Map<String, LeapType> spawnEaters = new HashMap<>();

    public LeapSpawnListener() {
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
        for (LeapType type : LeapType.values()) {
            this.spawnEaters.put(type.getTypeName(), type);
        }
    }

    @EventHandler
    public void onLeapSpawn(CreatureSpawnEvent event) {
        for (String tag : event.getEntity().getScoreboardTags()) {
            System.out.println(tag);
            LeapType leapType = spawnEaters.get(tag);
            if (leapType != null) {
                leapType.getLeapEater().eatSpawnEvent(event, leapType);
                break;
            } else {
                LeapPreConfig config = LeapConfigManager.get().getLeap(tag);
                if (config != null) LeapSpecificMisc.eatSpawnEvent(event, config);
            }
        }
    }

    public interface CustomSpawnEater {
        void eatSpawnEvent(CreatureSpawnEvent event, LeapType leapType);
    }
}
