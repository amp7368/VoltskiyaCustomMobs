package apple.voltskiya.custom_mobs.mobs.nms.parent.holder;


import apple.voltskiya.custom_mobs.mobs.nms.parent.register.RegisteredCustomMob;
import apple.voltskiya.custom_mobs.mobs.nms.parts.child.MobPartChild;
import apple.voltskiya.custom_mobs.mobs.nms.utils.UtilsPacket;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.List;

public interface NmsMob<
        TypeEntity extends Entity & NmsMob<TypeEntity, Config>,
        Config extends NmsMobConfig<TypeEntity, Config>> extends RegisteredCustomMob {

    default NmsMobHolder<TypeEntity, Config> verifyMobHolder() {
        NmsMobHolder<TypeEntity, Config> mobManager = getMobManager();
        if (mobManager == null) {
            mobManager = new NmsMobHolder<>(getRegister(), getEntity());
            setMobManager(mobManager);
        }
        return mobManager;
    }

    NmsMobHolder<TypeEntity, Config> getMobManager();

    void setMobManager(NmsMobHolder<TypeEntity, Config> mobManager);

    NmsMobEntitySupers getEntitySupers();

    NmsMobRegister<TypeEntity, Config> getRegister();

    TypeEntity getEntity();

    default Config getConfig() {
        return getMobManager().getConfig();
    }

    default boolean hasModel() {
        return verifyMobHolder().hasModel();
    }

    default void prepareNms(Location location, NBTTagCompound oldNbt) {
        if (oldNbt != null) {
            oldNbt.remove("UUID");
            this.load(oldNbt);
        }
        Entity entity = getEntity();
        entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    default void prepare() {
    }

    default void addChildren() {
        if (hasModel()) {
            NmsMobRegister<TypeEntity, Config> register = getRegister();
            Entity entity = getEntity();
            verifyMobHolder().addChildren(entity.getUniqueID(), register.getModel(), register.getModelName(), entity);
        }
    }

    default EntityTypes<?> nmsgetEntityType() {
        return getRegister().getEntityType();
    }

    default AttributeMapBase nmsgetAttributeMap() {
        return verifyMobHolder().getAttributeMap();
    }

    default void nmsmove(EnumMoveType enummovetype, Vec3D vec3d) {
        getEntitySupers().move(enummovetype, vec3d);
        if (hasModel()) {
            if (verifyMobHolder().getChildren() == null) addChildren();
            List<Packet<?>> packetsToSend = verifyMobHolder().move(true);
            UtilsPacket.sendPacketsToNearbyPlayers(packetsToSend, this.getEntity().getBukkitEntity().getLocation());
        }
    }

    /**
     * change worlds
     */
    @Nullable
    default Entity nmsb(WorldServer worldserver) {
        final Entity result = getEntitySupers().b(worldserver);
        if (hasModel() && result instanceof NmsMob<?, ?> nmsMob) {
            for (MobPartChild child : verifyMobHolder().getChildren()) {
                child.die();
            }
            nmsMob.addChildren();
        }
        return result;
    }


    default void nmsload(NBTTagCompound nbttagcompound) {
        nbttagcompound.setString("id", getRegister().registeredNameId());
        getEntitySupers().load(nbttagcompound);
    }


    default NBTTagCompound nmssave(NBTTagCompound nbttagcompound) {
        NBTTagCompound data = getEntitySupers().save(nbttagcompound);
        data.setString("id", getRegister().registeredNameId());
        return data;
    }


    default void nmsa(Entity.RemovalReason removalReason) {
        getEntitySupers().a(removalReason);
        if (hasModel())
            verifyMobHolder().killParts();
    }

    default EntityTypes<?> getEntityType() {
        return nmsgetEntityType();
    }

    default AttributeMapBase getAttributeMap() {
        return nmsgetAttributeMap();
    }

    default void move(EnumMoveType enummovetype, Vec3D vec3d) {
        nmsmove(enummovetype, vec3d);
    }

    default Entity b(WorldServer worldserver) {
        return nmsb(worldserver);
    }

    default void load(NBTTagCompound nbttagcompound) {
        nmsload(nbttagcompound);
    }

    default NBTTagCompound save(NBTTagCompound nbttagcompound) {
        return nmssave(nbttagcompound);
    }

    default void a(Entity.RemovalReason removalReason) {
        nmsa(removalReason);
    }

}
