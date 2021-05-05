package apple.voltskiya.custom_mobs.mobs.utils;

import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class UtilsPacket {
    public static final int VIEW_DISTANCE = Bukkit.getViewDistance();

    public static void sendPacketsToNearbyPlayers(List<Packet<?>> packetsToSend, Location location) {
        final Collection<? extends Player> nearbyPlayers = UpdatedPlayerList.getNearbyPlayers(location, VIEW_DISTANCE);
        int previousPriority = Thread.currentThread().getPriority();
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY); // try to send all these
        for (Player player : nearbyPlayers) {
            ((CraftPlayer) player).getHandle().playerConnection.networkManager.stopReading();
            for (Packet<? extends PacketListener> p : packetsToSend) {
                ((CraftPlayer) player).getHandle().playerConnection.networkManager.sendPacket(p);
            }
            ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.config().setAutoRead(true);
            ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.flush();
        }
        Thread.currentThread().setPriority(previousPriority);
    }
}
