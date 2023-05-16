package apple.voltskiya.custom_mobs.nms.nether.revenant;

import apple.voltskiya.custom_mobs.nms.base.NmsSpawner;
import apple.voltskiya.custom_mobs.nms.base.VoltEntityFactory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Skeleton;

public class RevenantSpawner extends NmsSpawner<MobRevenant, Skeleton> {

    @Override
    public String getName() {
        return "revenant";
    }

    @Override
    protected VoltEntityFactory<MobRevenant, Skeleton> getEntityFactory() {
        return MobRevenant::new;
    }

    @Override
    public EntityType<Skeleton> getEntityType() {
        return EntityType.SKELETON;
    }
}
