package apple.voltskiya.custom_mobs.nms.misc;

import apple.voltskiya.custom_mobs.nms.base.NmsSpawner;
import apple.voltskiya.custom_mobs.nms.base.VoltEntityFactory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;

public class MobHealthPackSpawner extends NmsSpawner<MobHealthPack, ArmorStand> {

    @Override
    public String getName() {
        return "health_pack";
    }

    @Override
    protected VoltEntityFactory<MobHealthPack, ArmorStand> getEntityFactory() {
        return MobHealthPack::new;
    }

    @Override
    public EntityType<ArmorStand> getEntityType() {
        return EntityType.ARMOR_STAND;
    }
}
