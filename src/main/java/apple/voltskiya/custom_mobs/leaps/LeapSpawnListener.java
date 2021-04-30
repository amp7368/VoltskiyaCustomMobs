package apple.voltskiya.custom_mobs.leaps;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.leaps.config.LeapConfig;
import apple.voltskiya.custom_mobs.leaps.config.LeapConfigManager;
import apple.voltskiya.custom_mobs.leaps.terra.LeapSpecificMisc;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.HashMap;
import java.util.Map;

public class LeapSpawnListener implements Listener {
    private final Map<String, LeapTypes> spawnEaters = new HashMap<>();

    public LeapSpawnListener() {
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
        for (LeapTypes type : LeapTypes.values()) {
            this.spawnEaters.put(type.getTypeName(), type);
        }
    }

    @EventHandler
    public void onLeapSpawn(CreatureSpawnEvent event) {
        for (String tag : event.getEntity().getScoreboardTags()) {
            System.out.println(tag);
            LeapTypes leapType = spawnEaters.get(tag);
            if (leapType != null) {
                leapType.getLeapEater().eatSpawnEvent(event, leapType);
                break;
            } else {
                LeapConfig config = LeapConfigManager.get().getLeap(tag);
                if (config != null) LeapSpecificMisc.eatSpawnEvent(event, config);
            }
        }
    }

    public interface CustomSpawnEater {
        void eatSpawnEvent(CreatureSpawnEvent event, LeapTypes leapType);
    }
}
