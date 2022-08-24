package apple.voltskiya.custom_mobs.mobs.nms.parent.holder;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.World;

@FunctionalInterface
public interface NmsMobConstructor<TypeEntity extends Entity & NmsMob<TypeEntity, Config>, Config extends NmsMobConfig<TypeEntity, Config>> {
    default EntityTypes.b<TypeEntity> builder() {
        return this::createNms;
    }

    TypeEntity createNms(EntityTypes<TypeEntity> entityTypes, World world);
}
