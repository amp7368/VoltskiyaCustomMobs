package apple.voltskiya.custom_mobs.dungeon.gui;

import apple.voltskiya.custom_mobs.gui.InventoryGuiPageScrollable;
import org.bukkit.inventory.InventoryHolder;

public class DungeonPageChests extends InventoryGuiPageScrollable {
    public DungeonPageChests(InventoryHolder holder) {
        super(holder);
    }

    @Override
    public String getName() {
        return "Chests";
    }

    @Override
    public int size() {
        return 54;
    }

    @Override
    protected int getScrollIncrement() {
        return 9;
    }
}
