package apple.voltskiya.custom_mobs.turret.player.gui;

import voltskiya.apple.utilities.util.gui.acd.page.InventoryGuiPageImplACD;

public class TurretPlayerGuiPage extends InventoryGuiPageImplACD<TurretPlayerGui> {
    public TurretPlayerGuiPage(TurretPlayerGui turretPlayerGui) {
        super(turretPlayerGui);
    }

    @Override
    public String getName() {
        return "Turret";
    }

    @Override
    public int size() {
        return 54;
    }
}
