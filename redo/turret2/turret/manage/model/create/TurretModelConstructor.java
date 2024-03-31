package apple.voltskiya.custom_mobs.turret.manage.model.create;

import apple.voltskiya.custom_mobs.turret.manage.model.config.TurretModelConfig;
import apple.voltskiya.custom_mobs.turret.manage.model.impl.TurretModelImpl;
import org.bukkit.Location;

@FunctionalInterface
public interface TurretModelConstructor {
    TurretModelImpl create(TurretModelConfig model, Location location);
}
