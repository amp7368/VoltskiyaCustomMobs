package apple.voltskiya.custom_mobs.mobs.parts;

import apple.voltskiya.custom_mobs.mobs.utils.UtilsPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.control.ControllerLook;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;

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
            List<Packet<?>> packetsToSend = new ArrayList<>();
            for (MobPartChild child : children) {
                packetsToSend.add(child.moveFromMother(true));
            }
            UtilsPacket.sendPacketsToNearbyPlayers(packetsToSend, this.a.getBukkitEntity().getLocation());
        }

    }
}
