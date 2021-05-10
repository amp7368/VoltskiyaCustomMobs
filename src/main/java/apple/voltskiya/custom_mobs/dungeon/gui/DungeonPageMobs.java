package apple.voltskiya.custom_mobs.dungeon.gui;

import apple.voltskiya.custom_mobs.gui.InventoryGui;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class DungeonPageMobs implements InventoryGui.InventoryGuiPage {
    public List<DungeonMobSlot> mobs = new ArrayList<>();

    @Override
    public String getName() {
        return "Dungeon Mobs";
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    @Override
    public void fillInventory() {

    }


    @Override
    public void dealWithClick(InventoryClickEvent event) {

    }

    @Override
    public int size() {
        return 0;
    }

    private class DungeonMobSlot {
    }
}
