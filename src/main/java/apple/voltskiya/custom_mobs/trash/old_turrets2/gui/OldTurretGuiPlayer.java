package apple.voltskiya.custom_mobs.trash.old_turrets2.gui;


import apple.voltskiya.custom_mobs.trash.old_turrets2.mobs.Old2TurretMobPlayer;
import voltskiya.apple.utilities.util.gui.InventoryGui;

public class OldTurretGuiPlayer extends InventoryGui {
    public OldTurretGuiPlayer(Old2TurretMobPlayer turretMob) {
        addPage(new OldTurretGuiPagePlayerSettings(turretMob, this));
    }

    public void update() {
        for (InventoryGuiPage page : pageMap)
            page.update();
    }
}
