package apple.voltskiya.custom_mobs.mobs.utils;

import apple.nms.decoding.packet.DecodePlayerConnection;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
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
            DecodePlayerConnection.getNetworkManager(((CraftPlayer) player).getHandle()).stopReading();
            for (Packet<? extends PacketListener> p : packetsToSend) {
                DecodePlayerConnection.getNetworkManager(((CraftPlayer) player).getHandle()).sendPacket(p);
            }
            DecodePlayerConnection.getChannel(((CraftPlayer) player).getHandle()).config().setAutoRead(true);
            DecodePlayerConnection.getChannel(((CraftPlayer) player).getHandle()).flush();
        }
        Thread.currentThread().setPriority(previousPriority);
    }
}
