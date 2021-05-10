package apple.voltskiya.custom_mobs.leaps;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.leaps.config.LeapConfigManager;
import apple.voltskiya.custom_mobs.leaps.config.LeapPreConfig;
import apple.voltskiya.custom_mobs.leaps.hellish_catalyst.LeapHellishCatalyst;
import apple.voltskiya.custom_mobs.leaps.misc.LeapSpecificMisc;
import apple.voltskiya.custom_mobs.leaps.pounce.LeapPounceNormal;
import apple.voltskiya.custom_mobs.leaps.pounce.LeapPounceUpwards;
import apple.voltskiya.custom_mobs.leaps.revenant.LeapRevenant;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.HashMap;
import java.util.Map;

public class LeapSpawnListener implements Listener {
    private final Map<String, LeapEater> spawnEaters = new HashMap<>();

    public LeapSpawnListener() {
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
        addEaters(new LeapHellishCatalyst(),
                new LeapRevenant(),
                new LeapPounceUpwards(),
                new LeapPounceNormal());
    }

    private void addEaters(LeapEater... eaters) {
        for (LeapEater eater : eaters) {
            eater.registerInDB();
            eater.eatMobs();
            spawnEaters.put(eater.getName(), eater);
        }
    }

    @EventHandler
    public void onLeapSpawn(CreatureSpawnEvent event) {
        for (String tag : event.getEntity().getScoreboardTags()) {
            LeapEater leapEater = spawnEaters.get(tag);
            if (leapEater != null)
                leapEater.eatAndRegisterEvent(event);
            else {
                LeapPreConfig config = LeapConfigManager.getLeap(tag);
                if (config != null) {
                    LeapSpecificMisc.eatSpawnEvent(event, tag, config);
                }
            }
        }
    }
}
