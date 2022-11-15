package apple.voltskiya.custom_mobs.nms.parent.qol;

import apple.voltskiya.custom_mobs.nms.parent.holder.NmsMobEntitySupers;
import apple.voltskiya.custom_mobs.nms.parent.utility.NmsSpawnWrapper;
import apple.voltskiya.custom_mobs.nms.parent.utility.NmsUtility;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

import java.util.Random;

public interface NmsHolderQOL<SelfEntity extends Entity & NmsHolderQOL<SelfEntity>> extends NmsUtility<SelfEntity> {
    @Override
    default NmsMobEntitySupers getEntitySupers() {
        return getSelfWrapper().getEntitySupers();
    }

    NmsSpawnWrapper<SelfEntity> getSpawner();

    NmsMobWrapperQOL<SelfEntity> getSelfWrapper();

    default Random getRandom() {
        return getSelfWrapper().getRandom();
    }

    default AttributeMap nmsgetAttributeMap() {
        return getSelfWrapper().getAttributeMap();
    }

    @Override
    default String getSaveId() {
        return getSpawner().name();
    }

    @Override
    default EntityType<?> nmsgetEntityType() {
        return getSpawner().EntityType();
    }

    default NmsMobWrapperQOL<SelfEntity> makeSelfWrapper() {
        return new NmsMobWrapperQOL<>(getSelfEntity());
    }
}
