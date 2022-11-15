package apple.voltskiya.custom_mobs.nms.parent.holder;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.World;

@FunctionalInterface
public interface NmsMobConstructor<TypeEntity extends Entity & NmsMob<TypeEntity, Config>, Config extends NmsMobConfig<TypeEntity, Config>> {
    default EntityType.b<TypeEntity> builder() {
        return this::createNms;
    }

    TypeEntity createNms(EntityType<TypeEntity> EntityType, World world);
}
