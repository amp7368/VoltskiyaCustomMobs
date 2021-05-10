package apple.voltskiya.custom_mobs.gui;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

public abstract class InventoryGuiPageSimple implements InventoryGui.InventoryGuiPage {
    protected final InventoryGui.InventoryGuiSlot[] clicking = new InventoryGui.InventoryGuiSlot[size()];
    private final Inventory inventory;

    public InventoryGuiPageSimple(InventoryHolder holder) {
        this.inventory = Bukkit.createInventory(holder, size(), getName());
        Arrays.fill(clicking, InventoryGuiSlotDoNothing.get());
    }

    public void setSlot(InventoryGui.InventoryGuiSlot item, Collection<Integer> slot) {
        for (Integer i : slot) {
            clicking[i] = item;
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void setSlot(InventoryGui.InventoryGuiSlot item, int... slot) {
        for (Integer i : slot) {
            clicking[i] = item;
        }
    }

    @Override
    public void fillInventory() {
        for (int i = 0; i < clicking.length; i++) {
            this.getInventory().setItem(i, clicking[i].getItem());
        }
    }


    @Override
    public void dealWithClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        if (slot < 0 || slot >= clicking.length) return;
        clicking[slot].dealWithClick(event);
    }
}
