package apple.voltskiya.custom_mobs.heartbeat.tick.listeners;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.heartbeat.tick.SpawnEater;
import apple.voltskiya.custom_mobs.heartbeat.tick.lost_soul.BlemishSpawnManager;
import apple.voltskiya.custom_mobs.heartbeat.tick.lost_soul.LostSoulManagerTicker;
import apple.voltskiya.custom_mobs.heartbeat.tick.orbital_strike.OrbitalStrikeManagerTicker;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.HashMap;
import java.util.Map;

public class MobSpawnListener implements Listener {
    private static final Map<String, SpawnEater> spawnEater = new HashMap<>();

    public MobSpawnListener() {
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
        spawnEater.put("lost_soul", new LostSoulManagerTicker());
        spawnEater.put("blemish_gateway", new BlemishSpawnManager());
        spawnEater.put("orbital_striker", new OrbitalStrikeManagerTicker());
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawn(CreatureSpawnEvent event) {
        for (String tag : event.getEntity().getScoreboardTags()) {
            System.out.println(tag);
            SpawnEater eater = spawnEater.get(tag);
            if (eater != null) eater.eatEvent(event);
        }
    }
}
