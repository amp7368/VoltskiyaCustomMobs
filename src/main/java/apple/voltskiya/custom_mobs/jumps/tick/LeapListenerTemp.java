package apple.voltskiya.custom_mobs.jumps.tick;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.jumps.config.Leap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class LeapListenerTemp implements Listener {
    private Location finalLocation = null;
    public LeapListenerTemp(){
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
    }
    @EventHandler
    public void click(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null) {
            this.finalLocation = event.getClickedBlock().getLocation().add(0, 1, 0);
        }
    }

    @EventHandler
    public void click(PlayerInteractAtEntityEvent event) {
        if (finalLocation != null) {
            int time = 60;
            int height = 50;
            new Leap(event.getRightClicked(), finalLocation, height, time);
        }
    }
}
