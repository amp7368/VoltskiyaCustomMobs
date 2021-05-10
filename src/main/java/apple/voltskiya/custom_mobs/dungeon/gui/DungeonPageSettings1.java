package apple.voltskiya.custom_mobs.dungeon.gui;

import apple.voltskiya.custom_mobs.gui.InventoryGui;
import apple.voltskiya.custom_mobs.gui.InventoryGuiPageSimple;
import apple.voltskiya.custom_mobs.gui.InventoryGuiSlotGeneric;
import apple.voltskiya.custom_mobs.util.minecraft.InventoryUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class DungeonPageSettings1 extends InventoryGuiPageSimple {
    private final DungeonGui dungeonGui;

    public DungeonPageSettings1(DungeonGui holder) {
        super(holder);
        this.dungeonGui = holder;
        setSlot(new Pos1(), 1);
        setSlot(new Pos2(), 2);
        setSlot(new InventoryGuiSlotGeneric((e1) -> holder.nextPage(1), InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Next Page", null)
        ), 8);
    }

    @Override
    public String getName() {
        return "Settings";
    }

    @Override
    public int size() {
        return 54;
    }

    private class Pos1 implements InventoryGui.InventoryGuiSlot {
        @Override
        public void dealWithClick(InventoryClickEvent event) {
        }

        @Override
        public ItemStack getItem() {
            final Location pos1 = dungeonGui.getDungeonScanner().getPos1();
            return InventoryUtils.makeItem(Material.WOODEN_PICKAXE,
                    1,
                    "Pos 1",
                    Collections.singletonList(
                            pos1 == null ? "Not set" :
                                    String.format(
                                            "(%d, %d, %d)", pos1.getBlockX(), pos1.getBlockY(), pos1.getBlockZ()
                                    )
                    )
            );
        }
    }

    private class Pos2 implements InventoryGui.InventoryGuiSlot {
        @Override
        public void dealWithClick(InventoryClickEvent event) {
        }

        @Override
        public ItemStack getItem() {
            final Location pos2 = dungeonGui.getDungeonScanner().getPos2();
            return InventoryUtils.makeItem(Material.STONE_PICKAXE,
                    1,
                    "Pos 2",
                    Collections.singletonList(
                            pos2 == null ? "Not set" :
                                    String.format(
                                            "(%d, %d, %d)", pos2.getBlockX(), pos2.getBlockY(), pos2.getBlockZ()
                                    )
                    )
            );
        }
    }
}
