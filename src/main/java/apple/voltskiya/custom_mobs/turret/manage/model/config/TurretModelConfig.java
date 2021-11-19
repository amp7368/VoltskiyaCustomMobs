package apple.voltskiya.custom_mobs.turret.manage.model.config;

import apple.voltskiya.custom_mobs.custom_model.handling.CustomModelConfig;

public class TurretModelConfig extends CustomModelConfig<TurretModelEntityConfig> {
    private TurretModelEntityConfig refilled;
    private TurretModelEntityConfig main;
    private TurretModelEntityConfig bow;
    private TurretModelEntityConfig durability;

    @Override
    public void addPiece(TurretModelEntityConfig part) {
        if (part.isMain()) this.main = part;
        if (part.isBow()) this.bow = part;
        if (part.isDurability()) this.durability = part;
        if (part.isRefilled()) this.refilled = part;
        addPart(part);
    }

    @Override
    public boolean validate() {
        return refilled != null && main != null && bow != null && durability != null;
    }
}
