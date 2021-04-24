package apple.voltskiya.custom_mobs.mobs.utils;

import net.minecraft.server.v1_16_R3.PacketPlayOutEntityStatus;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class UtilsPacket {
    public static void sendPacketsToAllPlayers(List<PacketPlayOutEntityStatus> packetsToSend) {
        int previousPriority = Thread.currentThread().getPriority();
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY); // try to send all these
        for (Player player : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) player).getHandle().playerConnection.networkManager.stopReading();
            for (PacketPlayOutEntityStatus p : packetsToSend) {
                ((CraftPlayer) player).getHandle().playerConnection.networkManager.sendPacket(p);
            }
            ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.config().setAutoRead(true);
            ((CraftPlayer) player).getHandle().playerConnection.networkManager.a();
        }
        Thread.currentThread().setPriority(previousPriority);
    }
}
