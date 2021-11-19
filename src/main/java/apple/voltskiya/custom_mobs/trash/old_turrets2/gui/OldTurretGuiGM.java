package apple.voltskiya.custom_mobs.trash.old_turrets2.gui;

import apple.voltskiya.custom_mobs.trash.old_turrets2.mobs.Old2TurretMobGM;
import voltskiya.apple.utilities.util.gui.InventoryGui;

public class OldTurretGuiGM extends InventoryGui {
    public OldTurretGuiGM(Old2TurretMobGM turretMobInfinite) {
        addPage(new OldTurretGuiPageGMSettings(turretMobInfinite, this));
        update();
    }

    public void update() {
        for (InventoryGui.InventoryGuiPage page : pageMap)
            page.update();
    }
}
