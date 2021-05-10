package apple.voltskiya.custom_mobs.dungeon.gui.mobs;

import apple.voltskiya.custom_mobs.dungeon.DungeonMobConfig;
import apple.voltskiya.custom_mobs.dungeon.gui.DungeonGui;
import apple.voltskiya.custom_mobs.gui.InventoryGuiPageScrollable;
import apple.voltskiya.custom_mobs.gui.InventoryGuiSlotGeneric;
import apple.voltskiya.custom_mobs.util.minecraft.InventoryUtils;
import org.bukkit.Material;

public class DungeonPageMobConfigs extends InventoryGuiPageScrollable {
    private final DungeonGui dungeonGui;

    public DungeonPageMobConfigs(DungeonGui dungeonGui) {
        super(dungeonGui);
        this.dungeonGui = dungeonGui;
        this.addMobs();
        this.setSlots();
    }

    private void addMobs() {
        for (DungeonMobConfig config : dungeonGui.getDungeonScanner().getMobConfigs())
            this.add(new DungeonMobConfigSlot(dungeonGui, config));
    }

    @Override
    public void setSlots() {
        super.setSlots();
        setSlot(new InventoryGuiSlotGeneric((e1) -> dungeonGui.nextPage(-1), InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Back", null)));
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
