package apple.voltskiya.custom_mobs.trash.dungeon.gui.mob_configs;

import apple.voltskiya.custom_mobs.trash.dungeon.gui.DungeonGui;
import apple.voltskiya.custom_mobs.trash.dungeon.scanner.DungeonMobInfo;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotScrollable;

public class DungeonMobInfoSlot extends InventoryGuiSlotScrollable {
    private final DungeonGui dungeonGui;
    private final DungeonMobInfo mob;

    public DungeonMobInfoSlot(DungeonGui dungeonGui, DungeonMobInfo mob) {
        this.dungeonGui = dungeonGui;
        this.mob = mob;
    }

    @Override
    public void dealWithClick(InventoryClickEvent event) {

    }

    @Override
    public ItemStack getItem() {
        return mob.toItem();
    }
}
