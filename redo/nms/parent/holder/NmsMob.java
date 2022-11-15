package apple.voltskiya.custom_mobs.nms.parent.holder;


import apple.voltskiya.custom_mobs.nms.parent.register.RegisteredCustomMob;
import apple.voltskiya.custom_mobs.nms.parent.utility.NmsUtility;
import apple.voltskiya.custom_mobs.nms.parts.NmsModel;
import apple.voltskiya.custom_mobs.nms.parts.child.MobPartChild;
import apple.voltskiya.custom_mobs.nms.utils.UtilsPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import javax.annotation.Nullable;
import java.util.List;

public interface NmsMob<
        SelfEntity extends Entity & NmsMob<SelfEntity, Config>,
        Config extends NmsMobConfig<SelfEntity, Config>
        > extends RegisteredCustomMob, NmsUtility<SelfEntity> {

    default NmsMobWrappedConfigable<SelfEntity, Config> verifyMobWrapper() {
        NmsMobWrappedConfigable<SelfEntity, Config> mobManager = getMobManager();
        if (mobManager == null) {
            mobManager = new NmsMobWrappedConfigable<>(getRegister(), getSelfEntity());
            setMobManager(mobManager);
        }
        return mobManager;
    }

    NmsMobWrappedConfigable<SelfEntity, Config> getMobManager();

    void setMobManager(NmsMobWrappedConfigable<SelfEntity, Config> mobManager);

    NmsMobRegisterConfigable<SelfEntity, Config> getRegister();

    @Override
    default String getSaveId() {
        return getRegister().getTag();
    }

    @Override
    default AttributeSupplier getAttributeSupplier() {
        return getRegister().getAttributeSupplier();
    }


    default Config getConfig() {
        return getMobManager().getConfig();
    }

    @Override
    @Nullable
    default NmsModel getSelfModel() {
        return getRegister().getModel();
    }

    default void addChildren() {
        NmsModel model = getRegister().getModel();
        if (model != null) {
            NmsMobRegisterConfigable<SelfEntity, Config> register = getRegister();
            Entity entity = getSelfEntity();
            verifyMobWrapper().addChildren(entity.getBukkitEntity().getUniqueId(), model, register.getModelName(), entity);
        }
    }

    @Override
    default void removePostHook() {
        if (hasModel())
            verifyMobWrapper().killParts();
        NmsUtility.super.removePostHook();
    }

    default void movePostHook() {
        if (hasModel()) {
            if (verifyMobWrapper().getChildren() == null) addChildren();
            List<Packet<?>> packetsToSend = verifyMobWrapper().move(true);
            UtilsPacket.sendPacketsToNearbyPlayers(packetsToSend, this.getSelfEntity().getBukkitEntity().getLocation());
        }
    }

    default EntityType<?> nmsgetEntityType() {
        return getRegister().getEntityType();
    }

    default AttributeMap nmsgetAttributeMap() {
        return verifyMobWrapper().getAttributeMap();
    }

    @Override
    default NmsMobEntitySupers getEntitySupers() {
        return getMobManager().getEntitySupers();
    }

    @Override
    default void changeWorldsPostHook(Entity result) {
        NmsUtility.super.changeWorldsPostHook(result);
        if (hasModel() && result instanceof NmsMob<?, ?> nmsMob) {
            for (MobPartChild child : verifyMobWrapper().getChildren()) {
                child.die();
            }
            nmsMob.addChildren();
        }
    }
}
