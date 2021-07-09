package apple.voltskiya.custom_mobs.turrets.gui;

import apple.voltskiya.custom_mobs.sql.DBItemStack;
import apple.voltskiya.custom_mobs.turrets.mobs.TurretMobPlayer;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.utilities.util.gui.InventoryGuiPageSimple;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotDoNothing;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotGeneric;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;
import voltskiya.apple.utilities.util.minecraft.MaterialUtils;

import java.util.List;

public class TurretGuiPagePlayerSettings extends InventoryGuiPageSimple {
    private final TurretMobPlayer turretMob;

    public TurretGuiPagePlayerSettings(TurretMobPlayer turretMob, InventoryHolder holder) {
        super(holder);
        this.turretMob = turretMob;
        setSlots();
        setSlot(new InventoryGuiSlotGeneric(e -> {
                }, InventoryUtils.makeItem(Material.BLACK_STAINED_GLASS_PANE, 1, "Bow slot", null)),
                30, 31, 32, 39, 41, 48, 49, 50);
    }

    private void setSlots() {
        Material bowMaterial = turretMob.getBowMaterial();
        if (bowMaterial != null) {
            setSlot(new InventoryGuiSlotGeneric(e -> {
                ItemStack bowItem = turretMob.getBowItem();
                if (bowItem != null)
                    e.getWhoClicked().getInventory().addItem(bowItem);
                turretMob.setBow(null);
            }, InventoryUtils.makeItem(bowMaterial, 1, (String) null, null)), 40);
        } else {
            setSlot(InventoryGuiSlotDoNothing.get(), 40);
        }
        List<DBItemStack> arrows = turretMob.getArrows();
        int size = arrows.size();
        for (int i = 0; i < size; i++) {
            int finalI = i;
            ItemStack item = arrows.get(i).toItem();
            if (item != null) {
                setSlot(new InventoryGuiSlotGeneric(e -> {
                    ItemStack arrowItem = turretMob.removeArrowAt(finalI);
                    if (arrowItem != null) {
                        e.getWhoClicked().getInventory().addItem(arrowItem);
                    }
                }, item), i);
            } else {
                setSlot(InventoryGuiSlotDoNothing.get(), i);
            }
            setSlot(new InventoryGuiSlotGeneric(e -> {
            }, InventoryUtils.makeItem(Material.BLACK_STAINED_GLASS_PANE, 1, "Arrow slot", null)), i + 9);
        }
        setSlot(new InventoryGuiSlotGeneric(e -> {
            turretMob.cycleMode();
        }, InventoryUtils.makeItem(Material.RED_TERRACOTTA, 1, turretMob.getMode().pretty(), null)), 53);

        setSlot(new InventoryGuiSlotGeneric(e -> {
        }, InventoryUtils.makeItem(Material.GOLDEN_APPLE, 1, String.format("Health, %.1f", turretMob.getHealth()), null)), 8);
    }


    @Override
    public void dealWithPlayerInventoryClick(InventoryClickEvent event) {
        @Nullable ItemStack item = event.getCurrentItem();
        if (item != null) {
            if (MaterialUtils.isBowLike(item.getType())) {
                event.setCurrentItem(turretMob.getBowItem());
                turretMob.setBow(item);
            } else if (MaterialUtils.isArrow(item.getType())) {
                turretMob.addArrows(item);
            }
        }
        event.setCancelled(true);
    }

    @Override
    public void fillInventory() {
        setSlots();
        super.fillInventory();
    }

    @Override
    public String getName() {
        return "Turret Settings";
    }

    @Override
    public int size() {
        return 54;
    }
}
