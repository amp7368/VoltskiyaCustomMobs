package apple.voltskiya.custom_mobs.dungeon.gui;

import apple.voltskiya.custom_mobs.gui.InventoryGui;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.Collection;

public class DungeonPageChests implements InventoryGui.InventoryGuiPage {
    @Override
    public String getName() {
        return null;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    @Override
    public void fillInventory() {

    }

    @Override
    public void setSlot(InventoryGui.InventoryGuiSlot item, Collection<Integer> slot) {

    }

    @Override
    public void setSlot(InventoryGui.InventoryGuiSlot item, int... slot) {

    }

    @Override
    public void dealWithClick(InventoryClickEvent event) {

    }

    @Override
    public int size() {
        return 0;
    }
}
