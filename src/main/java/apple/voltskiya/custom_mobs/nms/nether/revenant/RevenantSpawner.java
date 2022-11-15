package apple.voltskiya.custom_mobs.nms.nether.revenant;

import apple.voltskiya.custom_mobs.nms.NmsSpawner;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.world.entity.monster.Skeleton;

public class RevenantSpawner extends NmsSpawner<Skeleton> {

    public RevenantSpawner() {
        super("revenant");
    }


    @Override
    protected EntityFactory<Skeleton> getEntityFactory() {
        return NmsRevenant::new;
    }

    @Override
    public EntityType<Skeleton> getEntityType() {
        return EntityType.SKELETON;
    }
}
