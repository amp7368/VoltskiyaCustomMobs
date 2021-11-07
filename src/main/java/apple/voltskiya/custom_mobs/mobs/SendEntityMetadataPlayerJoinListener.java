package apple.voltskiya.custom_mobs.mobs;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class SendEntityMetadataPlayerJoinListener implements Listener {
    public SendEntityMetadataPlayerJoinListener() {
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
    }

    public void addMetadata(String s) {

    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
//        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
//            Player player = event.getPlayer();
//            if (player.isOnline()) {
//
//                PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata();
//
//                ((CraftPlayer) player).getHandle();
//            }
//        }, 1);
    }
}
