package apple.voltskiya.custom_mobs.mobs;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.mobs.nms.misc.MobTestSkeleton;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.network.protocol.game.PacketPlayOutTags;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.tags.ITagRegistry;
import net.minecraft.tags.Tags;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@CommandAlias("skele")
public class SkelePacketCommand extends BaseCommand {
    public SkelePacketCommand() {
        VoltskiyaPlugin.get().getCommandManager().registerCommand(this);
    }

    @Default
    public void command(Player player) {
        for (Entity entity : player.getNearbyEntities(20, 20, 20)) {
            net.minecraft.world.entity.Entity entityHandle = ((CraftEntity) entity).getHandle();
            System.out.println(entityHandle.getEntityType());
            if (entityHandle instanceof MobTestSkeleton) {
                MobTestSkeleton handle = (MobTestSkeleton) entityHandle;
                EntityPlayer playerHandle = ((CraftPlayer) player).getHandle();

                Map<ResourceKey<? extends IRegistry<?>>, Tags.a> map = new HashMap<>();
                ITagRegistry tagRegistry = playerHandle.getMinecraftServer().getTagRegistry();
                map.putAll(tagRegistry.a(IRegistryCustom.Dimension.a()));
                Tags.a idk = tagRegistry.a(IRegistry.Y.f()).a(IRegistry.Y);
                System.out.println(idk);
                map.put(IRegistry.Y.f(), idk);
                PacketPlayOutTags packetToSend = new PacketPlayOutTags(map);
                playerHandle.b.sendPacket(packetToSend);

                System.out.println("plz");

                playerHandle.b.a(handle.getEntityMetadataPacket(), (GenericFutureListener<Future<Void>>) voidFuture -> {
                    System.out.println("whoawhoa");
                });
                playerHandle.b.a(handle.getPacket(), (GenericFutureListener<Future<Void>>) voidFuture -> {
                    System.out.println("whoawhoa2");
                });
            }
        }
    }
}
