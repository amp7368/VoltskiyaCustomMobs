package apple.voltskiya.custom_mobs.turret.infinite;

import apple.voltskiya.custom_mobs.turret.manage.TurretTypeIdentifier;
import apple.voltskiya.custom_mobs.turret.parent.TurretBow;
import apple.voltskiya.custom_mobs.turret.parent.TurretMob;
import apple.voltskiya.custom_mobs.turret.parent.TurretTargeting;
import voltskiya.apple.utilities.util.gui.acd.InventoryGuiACD;

public class TurretMobInfinite extends TurretMob<TurretMobInfiniteConfig> {
    private final TurretTargettingInfinite turretTargettingInfinite = new TurretTargettingInfinite();

    @Override
    protected InventoryGuiACD makeGui() {
        return null;
    }

    @Override
    protected TurretTargeting getTargeting() {
        return turretTargettingInfinite;
    }

    @Override
    protected void kill() {
    }

    @Override
    protected TurretBow makeTurretBow() {
        return new TurretBowInfinite();
    }

    @Override
    protected TurretMobInfiniteConfig verifyConfig() {
        return TurretMobInfiniteConfig.get();
    }

    @Override
    public TurretTypeIdentifier getTypeIdentifier() {
        return null;
    }
}
