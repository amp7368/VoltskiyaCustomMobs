package apple.voltskiya.custom_mobs.dungeon.gui.chests;

import apple.voltskiya.custom_mobs.dungeon.gui.DungeonGui;
import apple.voltskiya.custom_mobs.dungeon.scanned.DungeonChestScanned;
import apple.voltskiya.custom_mobs.gui.InventoryGuiSlotScrollable;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

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
