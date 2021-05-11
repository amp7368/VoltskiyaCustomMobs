package apple.voltskiya.custom_mobs.dungeon.gui;

import apple.voltskiya.custom_mobs.dungeon.gui.chests.DungeonChestSlot;
import apple.voltskiya.custom_mobs.dungeon.scanned.DungeonChest;
import apple.voltskiya.custom_mobs.dungeon.scanned.DungeonScanned;
import apple.voltskiya.custom_mobs.gui.InventoryGuiPageScrollable;
import apple.voltskiya.custom_mobs.gui.InventoryGuiSlotGeneric;
import apple.voltskiya.custom_mobs.util.minecraft.InventoryUtils;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

public class DungeonPageChests extends InventoryGuiPageScrollable {
    private final DungeonGui dungeonGui;

    public DungeonPageChests(DungeonGui dungeonGui) {
        super(dungeonGui);
        this.dungeonGui = dungeonGui;
        addChests();
        setSlots();
    }

    private void addChests() {
        @Nullable DungeonScanned scanned = dungeonGui.getDungeonScanner().getDungeonInstance();
        if (scanned != null) {
            final List<DungeonChest> chests = scanned.getChests();
            chests.sort(Comparator.comparingDouble(o -> o.distance(dungeonGui.getPlayer())));
            for (DungeonChest chest : chests) {
                add(new DungeonChestSlot(dungeonGui, chest));
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
