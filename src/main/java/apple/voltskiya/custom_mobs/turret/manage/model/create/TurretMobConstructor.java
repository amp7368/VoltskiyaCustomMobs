package apple.voltskiya.custom_mobs.turret.manage.model.create;

import apple.voltskiya.custom_mobs.turret.manage.model.impl.TurretModelImpl;
import apple.voltskiya.custom_mobs.turret.parent.TurretMob;

@FunctionalInterface
public interface TurretMobConstructor<Mob extends TurretMob<?>> {
    Mob create(TurretModelImpl turretModel);
}
