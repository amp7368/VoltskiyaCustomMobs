package apple.voltskiya.custom_mobs.dungeon.gui.mobs;

import apple.voltskiya.custom_mobs.dungeon.gui.DungeonGui;
import apple.voltskiya.custom_mobs.dungeon.scanner.DungeonMobConfig;
import apple.voltskiya.custom_mobs.dungeon.scanner.DungeonMobInfo;
import apple.voltskiya.custom_mobs.gui.InventoryGuiPageScrollable;
import apple.voltskiya.custom_mobs.gui.InventoryGuiSlotGeneric;
import apple.voltskiya.custom_mobs.gui.InventoryGuiSlotScrollable;
import apple.voltskiya.custom_mobs.util.minecraft.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

class DungeonMobConfigSlot extends InventoryGuiSlotScrollable {
    private final DungeonGui dungeonGui;
    private final DungeonMobConfig config;

    public DungeonMobConfigSlot(DungeonGui dungeonGui, DungeonMobConfig config) {
        this.dungeonGui = dungeonGui;
        this.config = config;
    }

    @Override
    public void dealWithClick(InventoryClickEvent event) {
        new DungeonMobConfigPage();
    }

    @Override
    public ItemStack getItem() {
        return InventoryUtils.makeItem(
                Material.ARMOR_STAND,
                1,
                config.getName(),
                Collections.emptyList());
    }

    private class DungeonMobConfigPage extends InventoryGuiPageScrollable {
        public DungeonMobConfigPage() {
            super(dungeonGui);
            this.addMobs();
            this.setSlots();
            dungeonGui.setTempInventory(this);
        }


        private void addMobs() {
            for (DungeonMobInfo mob : config.getMobs())
                add(new DungeonMobInfoSlot(dungeonGui, mob));
        }

        @Override
        public void setSlots() {
            super.setSlots();
            this.setSlot(new InventoryGuiSlotGeneric((e) -> {
                dungeonGui.setTempInventory(null);
            }, InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Back", null)), 1);
        }

        @Override
        protected int getScrollIncrement() {
            return 9;
        }

        @Override
        public String getName() {
            return config.getName();
        }

        @Override
        public int size() {
            return 54;
        }
    }
}
