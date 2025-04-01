package apple.voltskiya.custom_mobs.nms.nether.revenant;

import apple.voltskiya.custom_mobs.nms.base.INmsMob;
import apple.voltskiya.custom_mobs.nms.base.NmsMob;
import apple.voltskiya.custom_mobs.nms.base.NmsMobSupers;
import apple.voltskiya.custom_mobs.nms.base.NmsSpawner;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;

public class MobRevenant extends Skeleton implements INmsMob<MobRevenant> {

    private static NmsSpawner<MobRevenant, ?> spawner;
    private NmsMob<MobRevenant> wrapper;

    public MobRevenant(EntityType<? extends Skeleton> entitytypes, Level world) {
        super(entitytypes, world);
    }

    public static NmsSpawner<MobRevenant, ?> spawner() {
        if (spawner != null) return spawner;
        return spawner = new RevenantSpawner();
    }

    @Override
    public MobRevenant getSelf() {
        return this;
    }

    @Override
    public NmsMob<MobRevenant> wrapper() {
        if (this.wrapper != null) return this.wrapper;
        return this.wrapper = createWrapper();
    }

    @Override
    public NmsSpawner<MobRevenant, ?> getInstSpawner() {
        return spawner();
    }

    @Override
    public NmsMobSupers<MobRevenant> makeEntitySupers() {
        return new NmsMobSupers<>(
            super::teleport,
            super::move,
            super::load,
            super::save,
            super::saveWithoutId,
            super::remove
        );
    }

    @Override
    public boolean canFireProjectileWeapon(ProjectileWeaponItem item) {
        return true;
    }

}
