package apple.voltskiya.custom_mobs.turret.gm;

import apple.voltskiya.custom_mobs.turret.gm.gui.TurretGuiGm;
import apple.voltskiya.custom_mobs.turret.manage.TurretTypeIdentifier;
import apple.voltskiya.custom_mobs.turret.manage.model.impl.TurretModelImpl;
import apple.voltskiya.custom_mobs.turret.parent.TurretMob;
import apple.voltskiya.custom_mobs.turret.parent.TurretTargeting;
import voltskiya.apple.utilities.util.gui.acd.InventoryGuiACD;

public class TurretMobGm extends TurretMob<TurretMobGmConfig> {

    private final TurretTargetingGm targeting = new TurretTargetingGm();

    public TurretMobGm(TurretModelImpl turretModel) {
        super(turretModel);
    }

    @Override
    protected InventoryGuiACD makeGui() {
        return new TurretGuiGm(this);
    }

    @Override
    protected TurretTargeting getTargeting() {
        return targeting;
    }


    @Override
    protected void kill() {
    }

    @Override
    protected TurretMobGmConfig verifyConfig() {
        return TurretMobGmConfig.get();
    }

    @Override
    public TurretTypeIdentifier getTypeIdentifier() {
        return TurretTypeIdentifier.GM;
    }

    public TurretTargetingGm.TurretTargetingGmMode getTargetingMode() {
        return targeting.getMode();
    }

    public void setTargetMode(TurretTargetingGm.TurretTargetingGmMode mode) {
        this.targeting.setMode(mode);
    }
}
