package apple.voltskiya.custom_mobs.turret.gm.gui;

import apple.voltskiya.custom_mobs.turret.gm.TurretMobGm;
import voltskiya.apple.utilities.util.gui.acd.InventoryGuiACD;

public class TurretGuiGm extends InventoryGuiACD {
    private final TurretMobGm turretMob;

    public TurretGuiGm(TurretMobGm turretMob) {
        this.turretMob = turretMob;
        addPage(new TurretMainPageGm(this));
    }

    public TurretMobGm getTurretMob() {
        return turretMob;
    }
}
