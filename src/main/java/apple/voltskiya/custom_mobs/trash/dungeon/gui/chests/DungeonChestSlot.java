package apple.voltskiya.custom_mobs.trash.dungeon.gui.chests;

import apple.voltskiya.custom_mobs.trash.dungeon.gui.DungeonGui;
import apple.voltskiya.custom_mobs.trash.dungeon.scanned.DungeonChestScanned;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotScrollable;

public class DungeonChestSlot extends InventoryGuiSlotScrollable {
    private final DungeonGui dungeonGui;
    private final DungeonChestScanned chest;

    public DungeonChestSlot(DungeonGui dungeonGui, DungeonChestScanned chest) {
        this.dungeonGui = dungeonGui;
        this.chest = chest;
    }

    @Override
    public void dealWithClick(InventoryClickEvent event) {
    }

    @Override
    public ItemStack getItem() {
        return chest.toItem(this.dungeonGui.getPlayer());
    }
}
