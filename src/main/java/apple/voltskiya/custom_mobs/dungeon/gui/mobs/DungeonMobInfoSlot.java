package apple.voltskiya.custom_mobs.dungeon.gui.mobs;

import apple.voltskiya.custom_mobs.dungeon.DungeonMobInfo;
import apple.voltskiya.custom_mobs.dungeon.gui.DungeonGui;
import apple.voltskiya.custom_mobs.gui.InventoryGuiSlotScrollable;
import apple.voltskiya.custom_mobs.util.minecraft.InventoryUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

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
        return InventoryUtils.makeItem(mob.getSpawnEgg(), 1, mob.getName(), null);
    }
}
