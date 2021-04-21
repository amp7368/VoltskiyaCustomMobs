package apple.voltskiya.custom_mobs.mobs.parts;

import apple.voltskiya.custom_mobs.mobs.NmsModelEntityConfig;
import apple.voltskiya.custom_mobs.util.EntityLocation;
import net.minecraft.server.v1_16_R3.ControllerLook;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityStatus;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class MobParts {
    private static final Map<NamespacedKey, MobPart> mobParts = new HashMap<>();

    static {
        MobPart.values(); // do the static part of MobPart
    }

    public static MobPartChild spawnMobPart(MobPartMother mother, NmsModelEntityConfig config) {
        NamespacedKey realKey = config.getEntity().type.getKey();
        MobPart mobPart = mobParts.get(realKey);
        if (mobPart != null) {
            return mobPart.spawnMobPart(mother, config);
        }
        return null;
    }

    private enum MobPart {
        ARMOR_STAND(EntityType.ARMOR_STAND.getKey(), MobPartArmorStand::spawnMobPart);

        private final BiFunction<MobPartMother, NmsModelEntityConfig, MobPartChild> spawner;

        MobPart(NamespacedKey namespace, BiFunction<MobPartMother, NmsModelEntityConfig, MobPartChild> spawner) {
            this.spawner = spawner;
            mobParts.put(namespace, this);
        }

        public MobPartChild spawnMobPart(MobPartMother mother, NmsModelEntityConfig config) {
            return spawner.apply(mother, config);
        }
    }

    public static class ControllerLookChildrenFollow extends ControllerLook {
        private final Iterable<MobPartChild> children;

        public ControllerLookChildrenFollow(EntityInsentient entityToLook, Iterable<MobPartChild> children) {
            super(entityToLook);
            this.children = children;
        }

        @Override
        public void a() {
            super.a();
            List<PacketPlayOutEntityStatus> packetsToSend = new ArrayList<>();
            for (MobPartChild child : children) {
                child.moveFromMother();
                packetsToSend.add(child.moveFromMother());
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                ((CraftPlayer) player).getHandle().playerConnection.networkManager.stopReading();
                for (PacketPlayOutEntityStatus p : packetsToSend) {
                    ((CraftPlayer) player).getHandle().playerConnection.networkManager.sendPacket(p);
                }
                ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.config().setAutoRead(true);
            }
        }
    }
}
