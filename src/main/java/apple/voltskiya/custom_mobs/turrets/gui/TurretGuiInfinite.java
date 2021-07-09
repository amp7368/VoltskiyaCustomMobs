package apple.voltskiya.custom_mobs.turrets.gui;

import apple.voltskiya.custom_mobs.turrets.mobs.TurretMobInfinite;
import voltskiya.apple.utilities.util.gui.InventoryGui;

public class TurretGuiInfinite extends InventoryGui {
    public TurretGuiInfinite(TurretMobInfinite turretMobInfinite) {
        addPage(new TurretGuipageInfiniteSettings(turretMobInfinite, this));
    }

    public void update() {
        for (InventoryGuiPage page : pageMap)
            page.update();
    }
}
