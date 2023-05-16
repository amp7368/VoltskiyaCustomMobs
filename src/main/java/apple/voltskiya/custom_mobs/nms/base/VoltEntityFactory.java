package apple.voltskiya.custom_mobs.nms.base;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public interface VoltEntityFactory<Self, MC extends Entity> {


    Self create(EntityType<MC> var1, Level var2);
}
