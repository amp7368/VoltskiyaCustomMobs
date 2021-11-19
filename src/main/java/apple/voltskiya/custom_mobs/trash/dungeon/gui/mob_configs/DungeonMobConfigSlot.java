package apple.voltskiya.custom_mobs.trash.dungeon.gui.mob_configs;

import apple.voltskiya.custom_mobs.trash.dungeon.gui.DungeonGui;
import apple.voltskiya.custom_mobs.trash.dungeon.scanner.DungeonMobConfig;
import apple.voltskiya.custom_mobs.trash.dungeon.scanner.DungeonMobInfo;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.utilities.util.gui.InventoryGui;
import voltskiya.apple.utilities.util.gui.InventoryGuiPageScrollable;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotGeneric;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotScrollable;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

public class DungeonMobConfigSlot extends InventoryGuiSlotScrollable {
    private final DungeonGui dungeonGui;
    private final DungeonMobConfig config;

    public DungeonMobConfigSlot(DungeonGui dungeonGui, DungeonMobConfig config) {
        this.dungeonGui = dungeonGui;
        this.config = config;
    }

    @Override
    public void dealWithClick(InventoryClickEvent event) {
        new DungeonMobConfigGui();
    }

    @Override
    public ItemStack getItem() {
        return config.toItem();
    }

    private class DungeonMobConfigGui extends InventoryGui {
        public DungeonMobConfigGui() {
            addPage(new DungeonMobConfigPage(this));
        }
    }

    private class DungeonMobConfigPage extends InventoryGuiPageScrollable {
        public DungeonMobConfigPage(DungeonMobConfigGui subGui) {
            super(subGui);
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
            }, InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Back", null)), 0);
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