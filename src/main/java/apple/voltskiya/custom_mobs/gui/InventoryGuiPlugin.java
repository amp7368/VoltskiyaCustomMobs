package apple.voltskiya.custom_mobs.gui;

import apple.voltskiya.custom_mobs.VoltskiyaModule;

public class InventoryGuiPlugin extends VoltskiyaModule {
    @Override
    public void enable() {
        new InventoryGuiListener();
    }

    @Override
    public String getName() {
        return "inventory_gui";
    }
}
