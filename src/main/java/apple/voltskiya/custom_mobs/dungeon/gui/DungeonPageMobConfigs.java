package apple.voltskiya.custom_mobs.dungeon.gui;

import apple.voltskiya.custom_mobs.dungeon.gui.mob_configs.DungeonMobConfigSlot;
import apple.voltskiya.custom_mobs.dungeon.scanner.DungeonMobConfig;
import apple.voltskiya.custom_mobs.dungeon.scanner.DungeonScanner;
import apple.voltskiya.custom_mobs.gui.InventoryGuiPageScrollable;
import apple.voltskiya.custom_mobs.gui.InventoryGuiSlotGeneric;
import org.bukkit.Material;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

public class DungeonPageMobConfigs extends InventoryGuiPageScrollable {
    private final DungeonGui dungeonGui;

    public DungeonPageMobConfigs(DungeonGui dungeonGui) {
        super(dungeonGui);
        this.dungeonGui = dungeonGui;
        final DungeonScanner scanner = dungeonGui.getDungeon().getScanner();
        if (scanner == null) {
            setSlot(new InventoryGuiSlotGeneric((e1) -> {
            }, InventoryUtils.makeItem(Material.RED_TERRACOTTA, 1, "The scanner is not set", null)), 4);
        } else {
            this.addMobs(scanner);
        }
        this.setSlots();
    }

    private void addMobs(DungeonScanner scanner) {
        for (DungeonMobConfig config : scanner.getMobConfigs())
            this.add(new DungeonMobConfigSlot(dungeonGui, config));
    }

    @Override
    public void setSlots() {
        super.setSlots();
        setSlot(new InventoryGuiSlotGeneric((e1) -> dungeonGui.nextPage(-1),
                InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Previous Page", null)), 0);
        setSlot(new InventoryGuiSlotGeneric((e1) -> dungeonGui.nextPage(1), InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Next Page", null)
        ), 8);
    }

    @Override
    protected int getScrollIncrement() {
        return 9;
    }

    @Override
    public String getName() {
        return "Mob Configs";
    }

    @Override
    public int size() {
        return 54;
    }

}
