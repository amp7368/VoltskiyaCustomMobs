package apple.voltskiya.custom_mobs.trash.old_turrets2.gui;

import apple.voltskiya.custom_mobs.trash.old_turrets2.mobs.Old2TurretMobInfinite;
import voltskiya.apple.utilities.util.gui.InventoryGui;

public class OldTurretGuiInfinite extends InventoryGui {
    public OldTurretGuiInfinite(Old2TurretMobInfinite turretMobInfinite) {
        addPage(new OldTurretGuipageInfiniteSettings(turretMobInfinite, this));
        update();
    }

    public void update() {
        for (InventoryGuiPage page : pageMap)
            page.update();
    }
}
