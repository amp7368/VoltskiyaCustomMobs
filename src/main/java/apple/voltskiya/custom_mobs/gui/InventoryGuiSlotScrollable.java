package apple.voltskiya.custom_mobs.gui;

public class InventoryGuiSlotScrollable extends InventoryGuiSlotDoNothing {
    private static InventoryGuiSlotDoNothing instance = null;

    public static InventoryGuiSlotDoNothing get() {
        if (instance == null) instance = new InventoryGuiSlotScrollable();
        return instance;
    }
}
