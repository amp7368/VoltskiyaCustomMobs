package apple.voltskiya.custom_mobs.abilities;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.abilities.tick.DeathEater;
import apple.voltskiya.custom_mobs.abilities.tick.reviver.dead.ReviveDeadManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashMap;
import java.util.Map;

public class MobTickDeathListener implements Listener {
    private static final Map<String, DeathEater> deathEater = new HashMap<>();

    public MobTickDeathListener() {
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
        deathEater.put("reviveable", new ReviveDeadManager());
    }

    @EventHandler(ignoreCancelled = true)
    public void onDeath(EntityDeathEvent event) {
        for (String tag : event.getEntity().getScoreboardTags()) {
            DeathEater eater = deathEater.get(tag);
            if (eater != null) eater.eatEvent(event);
        }
    }
}
