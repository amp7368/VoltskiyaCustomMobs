package apple.voltskiya.custom_mobs.dungeon.gui;

import apple.voltskiya.custom_mobs.dungeon.DungeonMobConfig;
import apple.voltskiya.custom_mobs.gui.InventoryGuiPageScrollable;
import apple.voltskiya.custom_mobs.gui.InventoryGuiSlotGeneric;
import apple.voltskiya.custom_mobs.gui.InventoryGuiSlotScrollable;
import apple.voltskiya.custom_mobs.util.minecraft.InventoryUtils;
import org.bukkit.Material;

import java.util.Collections;

public class DungeonPageMobConfigs extends InventoryGuiPageScrollable {
    private static final int SCROLL_INCREMENT = 8;
    private final DungeonGui dungeonGui;

    public DungeonPageMobConfigs(DungeonGui dungeonGui) {
        super(dungeonGui);
        this.dungeonGui = dungeonGui;
        addMobs();
        setSlots();
    }

    private void addMobs() {
        for (DungeonMobConfig config : dungeonGui.getDungeonScanner().getMobConfigs())
            this.add(new InventoryGuiSlotGeneric((c) -> {
            }, InventoryUtils.makeItem(
                    Material.ARMOR_STAND,
                    1,
                    config.nameToRepresentMob,
                    Collections.emptyList()
            )));
    }

    private void setSlots() {
        for (int i = 0; i < size(); i++) {
            if (i % 9 != 7)
                setSlot(InventoryGuiSlotScrollable.get(), i);
        }
        setSlot(new InventoryGuiSlotGeneric((e) -> this.next(-SCROLL_INCREMENT),
                InventoryUtils.makeItem(Material.REDSTONE_TORCH, 1, "Up", null)), 8);
        setSlot(new InventoryGuiSlotGeneric((e) -> this.next(-SCROLL_INCREMENT),
                InventoryUtils.makeItem(Material.LEVER, 1, "Down", null)), 17);
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
