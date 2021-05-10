package apple.voltskiya.custom_mobs.gui;

import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class InventoryGuiPageScrollable extends InventoryGuiPageSimple {
    private final List<InventoryGui.InventoryGuiSlot> scrollables = new ArrayList<>();
    private int currentIndex = 0;

    public InventoryGuiPageScrollable(InventoryHolder holder) {
        super(holder);
    }

    @Override
    public void fillInventory() {
        int scrollableIndex = currentIndex;
        for (int i = 0; i < clicking.length; i++) {
            if (clicking[i] instanceof InventoryGuiSlotScrollable && scrollableIndex < scrollables.size())
                this.getInventory().setItem(i, scrollables.get(scrollableIndex++).getItem());
            else
                this.getInventory().setItem(i, clicking[i].getItem());
        }
    }

    public void next(int index) {
        currentIndex += index;
    }

    public void add(InventoryGui.InventoryGuiSlot slot) {
        this.scrollables.add(slot);
    }

    public void remove(InventoryGui.InventoryGuiSlot slot) {
        this.scrollables.remove(slot);
    }

}
