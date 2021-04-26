package apple.voltskiya.custom_mobs.mobs;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.mobs.nether.eye_plant.MobEyePlant;
import apple.voltskiya.custom_mobs.mobs.nether.gremlin.MobWarpedGremlin;
import apple.voltskiya.custom_mobs.mobs.nether.parasite.MobParasite;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.HashMap;
import java.util.Map;

public class SpawnCustomMobListener implements Listener {
    public static final String CUSTOM_SPAWN_COMPLETE_TAG = "CUSTOM_SPAWN_COMPLETE";
    private final Map<String, CustomSpawnEater> spawnEaters = new HashMap<>();

    public SpawnCustomMobListener() {
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
        spawnEaters.put(MobEyePlant.REGISTERED_NAME, MobEyePlant::spawnEat);
        spawnEaters.put(MobWarpedGremlin.REGISTERED_NAME, MobWarpedGremlin::spawnEat);
        spawnEaters.put(MobParasite.REGISTERED_NAME, MobParasite::spawnEat);
    }

    @EventHandler(ignoreCancelled = true)
    public void spawnCustom(CreatureSpawnEvent event) {
        // if the entity wasn't already custom spawned
        if (!event.getEntity().getScoreboardTags().contains(CUSTOM_SPAWN_COMPLETE_TAG)) {
            for (String tag : event.getEntity().getScoreboardTags()) {
                CustomSpawnEater eater = spawnEaters.get(tag);
                if (eater != null) {
                    eater.eatSpawnEvent(event);
                    break;
                }
            }
        }
    }

    private interface CustomSpawnEater {
        void eatSpawnEvent(CreatureSpawnEvent event);
    }
}
