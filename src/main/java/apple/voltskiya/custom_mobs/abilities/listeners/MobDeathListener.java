package apple.voltskiya.custom_mobs.abilities.listeners;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.abilities.tick.DeathEater;
import apple.voltskiya.custom_mobs.abilities.tick.revive.ReviveDeadManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MobDeathListener implements @NotNull Listener {
    private static final Map<String, DeathEater> deathEater = new HashMap<>();

    public MobDeathListener() {
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
        try {
            deathEater.put("reviveable", new ReviveDeadManager());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            System.err.println("There was an issue with one of the config settings.\n" +
                    "You may have changed a setting that resulted in changing the type of data that was in one of the fields.");
            e.printStackTrace();
        }
        for (DeathEater spawnEater : deathEater.values()) {
            spawnEater.registerInDB();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDeath(EntityDeathEvent event) {
        for (String tag : event.getEntity().getScoreboardTags()) {
            DeathEater eater = deathEater.get(tag);
            if (eater != null) eater.eatEvent(event);
        }
    }
}
