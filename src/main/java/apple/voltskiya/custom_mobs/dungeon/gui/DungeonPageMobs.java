package apple.voltskiya.custom_mobs.dungeon.gui;

import apple.voltskiya.custom_mobs.dungeon.scanned.DungeonMobScanned;
import apple.voltskiya.custom_mobs.dungeon.scanned.DungeonScanned;
import apple.voltskiya.custom_mobs.gui.InventoryGuiPageScrollable;
import apple.voltskiya.custom_mobs.gui.InventoryGuiSlotGeneric;
import apple.voltskiya.custom_mobs.gui.InventoryGuiSlotScrollable;
import apple.voltskiya.custom_mobs.util.minecraft.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DungeonPageMobs extends InventoryGuiPageScrollable {
    private final DungeonGui dungeonGui;

    public DungeonPageMobs(DungeonGui dungeonGui) {
        super(dungeonGui);
        this.dungeonGui = dungeonGui;
        this.addMobs();
        this.setSlots();
    }

    private void addMobs() {
        @Nullable DungeonScanned scanned = dungeonGui.getDungeonScanner().getDungeonInstance();
        if (scanned != null) {
            for (DungeonMobScanned mob : scanned.getMobs()) {
                add(new DungeonMobSlot(mob));
            }
        }
    }

    @Override
    public void setSlots() {
        super.setSlots();
        setSlot(new InventoryGuiSlotGeneric((e1) -> dungeonGui.nextPage(-1),
                InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Previous Page", null)), 0);
        setSlot(new InventoryGuiSlotGeneric((e1) -> dungeonGui.nextPage(1),
                InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Next Page", null)
        ), 8);
    }

    @Override
    public String getName() {
        return "Dungeon Mobs";
    }

    @Override
    protected int getScrollIncrement() {
        return 9;
    }

    @Override
    public int size() {
        return 54;
    }

    private class DungeonMobSlot extends InventoryGuiSlotScrollable {
        private final DungeonMobScanned mob;

        public DungeonMobSlot(DungeonMobScanned mob) {
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
}
