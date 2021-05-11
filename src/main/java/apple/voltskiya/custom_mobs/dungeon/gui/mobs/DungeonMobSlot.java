package apple.voltskiya.custom_mobs.dungeon.gui.mobs;

import apple.voltskiya.custom_mobs.dungeon.gui.DungeonGui;
import apple.voltskiya.custom_mobs.dungeon.scanned.DungeonMobScanned;
import apple.voltskiya.custom_mobs.dungeon.scanned.DungeonScanned;
import apple.voltskiya.custom_mobs.gui.InventoryGui;
import apple.voltskiya.custom_mobs.gui.InventoryGuiPageSimple;
import apple.voltskiya.custom_mobs.gui.InventoryGuiSlotGeneric;
import apple.voltskiya.custom_mobs.gui.InventoryGuiSlotScrollable;
import apple.voltskiya.custom_mobs.util.VectorUtils;
import apple.voltskiya.custom_mobs.util.minecraft.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class DungeonMobSlot extends InventoryGuiSlotScrollable {
    private final DungeonGui dungeonGui;
    private final DungeonMobScanned mob;
    private @NotNull
    final DungeonScanned dungeonInstance;

    public DungeonMobSlot(DungeonGui dungeonGui, DungeonMobScanned mob) {
        this.dungeonGui = dungeonGui;
        this.mob = mob;
        final DungeonScanned dungeonInstance = dungeonGui.getDungeonScanner().getDungeonInstance();
        if (dungeonInstance == null) throw new IllegalStateException("The dungeon isn't real");
        this.dungeonInstance = dungeonInstance;
    }

    @Override
    public void dealWithClick(InventoryClickEvent event) {
        new DungeonMobSubGui();
    }

    @Override
    public ItemStack getItem() {
        return mob.toItem(this.dungeonGui.getPlayer());
    }

    private class DungeonMobSubGui extends InventoryGui {
        public DungeonMobSubGui() {
            addPage(new DungeonMobSubPage(this));
        }
    }

    private class DungeonMobSubPage extends InventoryGuiPageSimple {


        public DungeonMobSubPage(DungeonMobSubGui dungeonMobSubGui) {
            super(dungeonMobSubGui);
            this.setSlots();
            dungeonGui.setTempInventory(this);
        }

        private void setSlots() {
            this.setSlot(new InventoryGuiSlotGeneric((e) -> {
                dungeonGui.setTempInventory(null);
                try {
                    dungeonInstance.save();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }, InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Back", null)), 0);
            this.setSlot(new InventoryGuiSlotGeneric((e) -> {
                mob.rotate(15);
            }, InventoryUtils.makeItem(Material.MAGENTA_DYE, 1, "Rotate 15 Degrees Clockwise", null)
            ), 11);
            this.setSlot(new InventoryGuiSlotGeneric((e) -> {
                mob.rotate(1);
            }, InventoryUtils.makeItem(Material.PINK_DYE, 1, "Rotate 1 Degree Clockwise", null)
            ), 12);
            this.setSlot(new InventoryGuiSlotGeneric((e) -> {
                mob.rotate(-1);
            }, InventoryUtils.makeItem(Material.BLUE_DYE, 1, "Rotate 1 Degree CounterClockwise", null)
            ), 13);
            this.setSlot(new InventoryGuiSlotGeneric((e) -> {
                mob.rotate(-15);
            }, InventoryUtils.makeItem(Material.CYAN_DYE, 1, "Rotate 15 Degrees CounterClockwise", null)
            ), 14);
            this.setSlot(new InventoryGuiSlotGeneric((e) -> {
                mob.pitchAdd(1);
            }, InventoryUtils.makeItem(Material.YELLOW_DYE, 1, "Pitch add 1 Degree", null)
            ), 21);
            this.setSlot(new InventoryGuiSlotGeneric((e) -> {
                mob.pitchAdd(-1);
            }, InventoryUtils.makeItem(Material.ORANGE_DYE, 1, "Pitch add -1 Degree", null)
            ), 22);
            this.setSlot(new MobLocation(), 18);
        }

        @Override
        public String getName() {
            return mob.getName();
        }

        @Override
        public int size() {
            return 54;
        }

        private class MobLocation implements InventoryGui.InventoryGuiSlot {
            @Override
            public void dealWithClick(InventoryClickEvent event) {
            }

            @Override
            public ItemStack getItem() {
                Vector xyz = mob.getLocation();
                if (xyz == null) {
                    return InventoryUtils.makeItem(Material.BLACK_STAINED_GLASS, 1, "Entity is not loaded anymore", null);
                } else {
                    List<String> distance = Collections.singletonList(String.format("%.2f blocks away",
                            (VectorUtils.magnitude(dungeonGui.getPlayer().getLocation().toVector().subtract(xyz)))
                    ));
                    return InventoryUtils.makeItem(Material.BLACK_STAINED_GLASS,
                            1,
                            String.format("[ %d , %d , %d]", xyz.getBlockX(), xyz.getBlockY(), xyz.getBlockZ()),
                            distance);
                }
            }

        }
    }
}
