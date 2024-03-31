package apple.voltskiya.custom_mobs.turret.manage.model.create;

import apple.voltskiya.custom_mobs.turret.base.TurretMob;
import apple.voltskiya.custom_mobs.turret.manage.model.impl.TurretModelImpl;

@FunctionalInterface
public interface TurretMobConstructor<Mob extends TurretMob<?>> {

    Mob create(TurretModelImpl turretModel);
}
