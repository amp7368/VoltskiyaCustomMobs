package apple.voltskiya.custom_mobs.nms.nether.revenant;

import apple.voltskiya.custom_mobs.nms.NmsMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;

public class NmsRevenant extends Skeleton implements NmsMob {

    public NmsRevenant(EntityType<? extends Skeleton> entitytypes, Level world) {
        super(entitytypes, world);
    }

    @Override
    public boolean canFireProjectileWeapon(ProjectileWeaponItem itemprojectileweapon) {
        return true;
    }
}