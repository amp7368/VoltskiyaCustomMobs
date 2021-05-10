package apple.voltskiya.custom_mobs.gui;

import apple.voltskiya.custom_mobs.util.minecraft.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class InventoryGuiPageScrollable extends InventoryGuiPageSimple {
    private final List<InventoryGuiSlotScrollable> scrollables = new ArrayList<>();
    private int currentIndex = 0;

    public InventoryGuiPageScrollable(InventoryHolder holder) {
        super(holder);
    }

    @Override
    public void fillInventory() {
        int scrollableIndex = currentIndex;
        for (int i = 0; i < clicking.length; i++) {
            if (clicking[i] instanceof InventoryGuiSlotScrollable) {
                if (scrollableIndex < scrollables.size()) {
                    this.setSlot(scrollables.get(scrollableIndex++), i);
                } else {
                    this.setSlot(InventoryGuiSlotScrollable.get());
                }
            }
        }
        super.fillInventory();
    }

    public void setSlots() {
        for (int i = 9; i < size(); i++) {
            if (i % 9 != 7)
                setSlot(InventoryGuiSlotScrollable.get(), i);
        }
        setSlot(new InventoryGuiSlotGeneric((e) -> this.next(-getScrollIncrement()),
                InventoryUtils.makeItem(Material.REDSTONE_TORCH, 1, "Up", null)), 17);
        setSlot(new InventoryGuiSlotGeneric((e) -> this.next(getScrollIncrement()),
                InventoryUtils.makeItem(Material.LEVER, 1, "Down", null)), 26);
    }


    public void next(int index) {
        currentIndex += index;
    }

    protected abstract int getScrollIncrement();

    public void add(InventoryGuiSlotScrollable slot) {
        this.scrollables.add(slot);
    }

    public void remove(InventoryGuiSlotScrollable slot) {
        this.scrollables.remove(slot);
    }
}
