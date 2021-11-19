package apple.voltskiya.custom_mobs.turret.player.gui;

import voltskiya.apple.utilities.util.gui.acd.InventoryGuiACD;

public class TurretPlayerGui extends InventoryGuiACD {
    public TurretPlayerGui() {
        addPage(new TurretPlayerGuiPage(this));
    }
}
