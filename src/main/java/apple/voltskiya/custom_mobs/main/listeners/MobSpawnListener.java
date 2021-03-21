package apple.voltskiya.custom_mobs.main.listeners;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.tick.SpawnEater;
import apple.voltskiya.custom_mobs.tick.charger.ChargerManagerTicker;
import apple.voltskiya.custom_mobs.tick.hell_blazer.HellGuardManagerTicker;
import apple.voltskiya.custom_mobs.tick.lost_soul.BlemishSpawnManager;
import apple.voltskiya.custom_mobs.tick.lost_soul.LostSoulManagerTicker;
import apple.voltskiya.custom_mobs.tick.orbital_strike.large.LargeOrbitalStrikeManagerTicker;
import apple.voltskiya.custom_mobs.tick.orbital_strike.small.SmallOrbitalStrikeManagerTicker;
import apple.voltskiya.custom_mobs.tick.revive.ReviverManagerTicker;
import apple.voltskiya.custom_mobs.tick.warper.WarperManagerTicker;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MobSpawnListener implements Listener {
    private static final Map<String, SpawnEater> spawnEater = new HashMap<>();

    public MobSpawnListener() {
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
        try {
            spawnEater.put("lost_soul", new LostSoulManagerTicker());
            spawnEater.put("blemish_gateway", new BlemishSpawnManager());
            spawnEater.put("orbital_striker", new LargeOrbitalStrikeManagerTicker());
            spawnEater.put("orbital_striker_small", new SmallOrbitalStrikeManagerTicker());
            spawnEater.put("warper", new WarperManagerTicker());
            spawnEater.put("reviver", new ReviverManagerTicker());
            spawnEater.put("hell_blazer", new HellGuardManagerTicker());
            spawnEater.put("charger", new ChargerManagerTicker());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            System.err.println("There was an issue with one of the config settings.\n" +
                    "You may have changed a setting that resulted in changing the type of data that was in one of the fields.");
            e.printStackTrace();
        }
        for (SpawnEater spawnEater : spawnEater.values()) {
            spawnEater.registerInDB();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawn(CreatureSpawnEvent event) {
        for (String tag : event.getEntity().getScoreboardTags()) {
            SpawnEater eater = spawnEater.get(tag);
            if (eater != null) eater.eatEvent(event);
        }
    }
}