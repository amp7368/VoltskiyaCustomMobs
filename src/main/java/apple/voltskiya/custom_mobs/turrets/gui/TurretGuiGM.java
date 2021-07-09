package apple.voltskiya.custom_mobs.turrets.gui;

import apple.voltskiya.custom_mobs.turrets.mobs.TurretMobGM;
import voltskiya.apple.utilities.util.gui.InventoryGui;

public class TurretGuiGM extends InventoryGui {
    public TurretGuiGM(TurretMobGM turretMobInfinite) {
        addPage(new TurretGuiPageGMSettings(turretMobInfinite, this));
        update();
    }

    public void update() {
        for (InventoryGui.InventoryGuiPage page : pageMap)
            page.update();
    }
}
