package apple.voltskiya.custom_mobs.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class InventoryGuiSlotGeneric implements InventoryGui.InventoryGuiSlot {
    private final Consumer<InventoryClickEvent> dealWithEvent;
    private final ItemStack item;

    public InventoryGuiSlotGeneric(Consumer<InventoryClickEvent> dealWithEvent, ItemStack item) {
        this.dealWithEvent = dealWithEvent;
        this.item = item;
    }

    @Override
    public void dealWithClick(InventoryClickEvent event) {
        dealWithEvent.accept(event);
    }

    @Override
    public ItemStack getItem() {
        return item;
    }
}
