package apple.voltskiya.custom_mobs.mobs.nms.parent.qol;

import apple.voltskiya.custom_mobs.mobs.nms.parent.utility.NmsSpawnWrapperModel;
import apple.voltskiya.custom_mobs.mobs.nms.parent.utility.NmsUtility;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;

public interface NmsModelHolderQOL<SelfEntity extends EntityInsentient & NmsUtility<SelfEntity> & NmsModelHolderQOL<SelfEntity>>
        extends NmsHolderQOL<SelfEntity> {

    default void addChildrenPost() {
    }

    @Override
    default void preparePostHook() {
        NmsHolderQOL.super.preparePostHook();
        getSelfWrapper().verifyChildren();
    }

    default void movePostHook() {
        NmsHolderQOL.super.movePostHook();
        getSelfWrapper().moveChildren();
    }

    NmsSpawnWrapperModel<SelfEntity> getSpawner();

    NmsMobWrapperQOLModel<SelfEntity> getSelfWrapper();

    @Override
    default void removePostHook() {
        NmsHolderQOL.super.removePostHook();
        getSelfWrapper().die();
    }

    @Override
    default void changeWorldsPostHook(Entity result) {
        NmsHolderQOL.super.changeWorldsPostHook(result);
        getSelfWrapper().die();
    }

    @Override
    default NmsModel getSelfModel() {
        return getSpawner().getSelfModel();
    }

    @Override
    default NmsMobWrapperQOLModel<SelfEntity> makeSelfWrapper() {
        return new NmsMobWrapperQOLModel<>(getSelfEntity());
    }
}
