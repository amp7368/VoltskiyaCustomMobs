package apple.voltskiya.custom_mobs.abilities.tick.lost_soul;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class BlemishDeathListener implements Listener {
    public BlemishDeathListener(){
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
    }
    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity().getScoreboardTags().contains(BlemishSpawnManager.NAME)) {
            BlemishSpawnManager.shoot(event.getEntity(), event.getEntity().getLocation(), 5);
        }
    }
}
