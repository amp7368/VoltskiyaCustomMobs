package apple.voltskiya.custom_mobs.turret.manage.model.impl;

import apple.voltskiya.custom_mobs.custom_model.spawning.CustomModelEntityImpl;
import apple.voltskiya.custom_mobs.turret.manage.model.config.TurretModelEntityConfig;
import org.bukkit.entity.Entity;

public class TurretModelEntityImpl extends CustomModelEntityImpl<TurretModelEntityConfig> {
    private final boolean isMain;
    private final boolean isBow;
    private final boolean isDurability;
    private final boolean isRefilled;

    public TurretModelEntityImpl(TurretModelEntityConfig entityModel, Entity spawned) {
        super(entityModel, spawned);
        isMain = entityModel.isMain();
        isBow = entityModel.isBow();
        isDurability = entityModel.isDurability();
        isRefilled = entityModel.isRefilled();
    }

    public boolean isMain() {
        return this.isMain;
    }

    public boolean isDurability() {
        return this.isDurability;
    }

    public boolean isBow() {
        return this.isBow;
    }

    public boolean isRefilled() {
        return this.isRefilled;
    }
}
