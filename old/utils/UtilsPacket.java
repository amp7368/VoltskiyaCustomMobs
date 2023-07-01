package apple.voltskiya.custom_mobs.nms.utils;

import apple.nms.decoding.packet.DecodePlayerConnection;
import io.netty.channel.Channel;
import java.util.Collection;
import java.util.List;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;

public class UtilsPacket {

    public static final int VIEW_DISTANCE = Bukkit.getViewDistance();

    public static void sendPacketsToNearbyPlayers(List<Packet<?>> packetsToSend,
        Location location) {
        final Collection<org.bukkit.entity.Player> nearbyPlayers = location.getNearbyPlayers(
            VIEW_DISTANCE);
        for (org.bukkit.entity.Player player : nearbyPlayers) {
            ServerPlayer handle = ((CraftPlayer) player).getHandle();
            Connection networkManager = DecodePlayerConnection.getNetworkManager(handle);
            for (Packet<?> p : packetsToSend) {
                networkManager.send(p);
            }
            Channel channel = DecodePlayerConnection.getChannel(handle);
            channel.config().setAutoRead(true);
            channel.flush();
        }
    }
}
