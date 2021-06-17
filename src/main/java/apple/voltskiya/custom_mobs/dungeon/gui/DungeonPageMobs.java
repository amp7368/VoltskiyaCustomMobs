package apple.voltskiya.custom_mobs.dungeon.gui;

import apple.voltskiya.custom_mobs.dungeon.gui.mobs.DungeonMobSlot;
import apple.voltskiya.custom_mobs.dungeon.scanned.DungeonMobScanned;
import apple.voltskiya.custom_mobs.dungeon.scanned.DungeonScanned;
import apple.voltskiya.custom_mobs.gui.InventoryGuiPageScrollable;
import apple.voltskiya.custom_mobs.gui.InventoryGuiSlotGeneric;
import apple.voltskiya.custom_mobs.util.minecraft.InventoryUtils;
import org.bukkit.Material;

import java.util.Comparator;
import java.util.List;

public class DungeonPageMobs extends InventoryGuiPageScrollable {
    private final DungeonGui dungeonGui;

    public DungeonPageMobs(DungeonGui dungeonGui) {
        super(dungeonGui);
        this.dungeonGui = dungeonGui;
        DungeonScanned scanned = dungeonGui.getDungeon().getScanned();
        if (scanned == null) {
            setSlot(new InventoryGuiSlotGeneric((e1) -> {
            }, InventoryUtils.makeItem(Material.RED_TERRACOTTA, 1, "The dungeon layout is not set", null)), 4);
        } else {
            this.addMobs(scanned);
        }
        this.setSlots();
    }

    private void addMobs(DungeonScanned scanned) {
        final List<DungeonMobScanned> mobs = scanned.getMobs();
        mobs.sort(Comparator.comparingDouble(o -> o.distance(dungeonGui.getPlayer())));
        for (DungeonMobScanned mob : mobs) {
            add(new DungeonMobSlot(dungeonGui, mob));
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

}
