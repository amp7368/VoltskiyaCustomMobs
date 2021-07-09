package apple.voltskiya.custom_mobs.turrets.gui;


import apple.voltskiya.custom_mobs.turrets.mobs.TurretMobPlayer;
import voltskiya.apple.utilities.util.gui.InventoryGui;

public class TurretGuiPlayer extends InventoryGui {
    public TurretGuiPlayer(TurretMobPlayer turretMob) {
        addPage(new TurretGuiPagePlayerSettings(turretMob, this));
    }

    public void update() {
        for (InventoryGuiPage page : pageMap)
            page.update();
    }
}
