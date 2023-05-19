package apple.voltskiya.custom_mobs.nms.nether.angered_soul;

import apple.voltskiya.custom_mobs.nms.base.NmsSpawner;
import apple.voltskiya.custom_mobs.nms.base.VoltEntityFactory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Skeleton;

public class AngeredSoulSpawner extends NmsSpawner<MobAngeredSoul, Skeleton> {

    @Override
    public String getName() {
        return "angered_soul";
    }

    @Override
    protected VoltEntityFactory<MobAngeredSoul, Skeleton> getEntityFactory() {
        return MobAngeredSoul::new;
    }

    @Override
    public EntityType<Skeleton> getEntityType() {
        return EntityType.SKELETON;
    }
}
