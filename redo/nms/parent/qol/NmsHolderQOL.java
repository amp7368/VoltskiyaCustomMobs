package apple.voltskiya.custom_mobs.mobs.nms.parent.qol;

import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobEntitySupers;
import apple.voltskiya.custom_mobs.mobs.nms.parent.utility.NmsSpawnWrapper;
import apple.voltskiya.custom_mobs.mobs.nms.parent.utility.NmsUtility;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;

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

    default AttributeMapBase nmsgetAttributeMap() {
        return getSelfWrapper().getAttributeMap();
    }

    @Override
    default String getSaveId() {
        return getSpawner().name();
    }

    @Override
    default EntityTypes<?> nmsgetEntityType() {
        return getSpawner().entityTypes();
    }

    default NmsMobWrapperQOL<SelfEntity> makeSelfWrapper() {
        return new NmsMobWrapperQOL<>(getSelfEntity());
    }
}
