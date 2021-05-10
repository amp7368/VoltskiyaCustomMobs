package apple.voltskiya.custom_mobs.gui;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryGuiSlotDoNothing implements InventoryGui.InventoryGuiSlot {
    private static InventoryGuiSlotDoNothing instance = null;

    public static InventoryGuiSlotDoNothing get() {
        if (instance == null) instance = new InventoryGuiSlotDoNothing();
        return instance;
    }

    @Override
    public void dealWithClick(InventoryClickEvent event) {
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Material.AIR);
    }
}
