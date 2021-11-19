package apple.voltskiya.custom_mobs.turret.player;

import apple.voltskiya.custom_mobs.turret.manage.TurretTypeIdentifier;
import apple.voltskiya.custom_mobs.turret.manage.model.impl.TurretModelImpl;
import apple.voltskiya.custom_mobs.turret.parent.TurretMob;
import apple.voltskiya.custom_mobs.turret.parent.TurretTargeting;
import voltskiya.apple.utilities.util.gui.acd.InventoryGuiACD;

public class TurretMobPlayer extends TurretMob<TurretMobPlayerConfig> {
    public TurretMobPlayer(TurretModelImpl turretModel) {
        super(turretModel);
    }

    @Override
    protected InventoryGuiACD makeGui() {
        return null;
    }

    @Override
    protected TurretTargeting getTargeting() {
        return null;
    }

    @Override
    protected void kill() {

    }

    @Override
    protected TurretMobPlayerConfig verifyConfig() {
        return TurretMobPlayerConfig.get();
    }

    @Override
    public TurretTypeIdentifier getTypeIdentifier() {
        return TurretTypeIdentifier.PLAYER;
    }
}
