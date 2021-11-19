package apple.voltskiya.custom_mobs.turret.gm.gui;

import apple.utilities.util.Pretty;
import apple.voltskiya.custom_mobs.turret.gm.TurretMobGm;
import apple.voltskiya.custom_mobs.turret.gm.TurretMobGmConfig;
import apple.voltskiya.custom_mobs.turret.parent.TurretMob;
import apple.voltskiya.custom_mobs.turret.parent.TurretUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.utilities.util.gui.acd.page.InventoryGuiPageImplACD;
import voltskiya.apple.utilities.util.gui.acd.slot.InventoryGuiButtonTemplate;
import voltskiya.apple.utilities.util.gui.acd.slot.InventoryGuiSlotDoNothingACD;
import voltskiya.apple.utilities.util.gui.acd.slot.InventoryGuiSlotImplACD;
import voltskiya.apple.utilities.util.gui.acd.slot.cycle.InventoryGuiSlotCycleACD;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;
import voltskiya.apple.utilities.util.minecraft.MaterialUtils;
import voltskiya.apple.utilities.util.minecraft.inventory.InventoryModify;
import voltskiya.apple.utilities.util.minecraft.inventory.InventoryModifyResult;

import java.util.List;

public class TurretMainPageGm extends InventoryGuiPageImplACD<TurretGuiGm> {
    public TurretMainPageGm(TurretGuiGm turretGuiGm) {
        super(turretGuiGm);
    }

    @Override
    public void initialize() {
        setSlot(InventoryGuiButtonTemplate.blackGlassDoNothing(), 18, 19, 20);
        setSlot(InventoryGuiButtonTemplate.blackGlassDoNothing(),
                30, 31, 32, 39, 41, 48, 49, 50);
    }

    @Override
    public void refreshPageItems() {
        setArrowSlots();
        setBowSlot();
        setHealthSlot();
        setTargettingSlot();
    }

    private void setTargettingSlot() {
        setSlot(new InventoryGuiSlotCycleACD<>(getTurretMob()::getTargetingMode, getTurretMob()::setTargetMode), 8);
    }

    private void setHealthSlot() {
        setSlot(new InventoryGuiSlotImplACD(this::removeRepairFromUser, makeRepairItem()), 53);
    }

    @NotNull
    private ItemStack makeRepairItem() {
        TurretMob<TurretMobGmConfig> turretMob = getTurretMob();
        return InventoryUtils.makeItem(
                turretMob.getRepairMaterial(),
                1,
                String.format("%d Health", (int) turretMob.getHealth()),
                List.of(
                        String.format("1 %s repairs %d Health",
                                Pretty.spaceEnumWords(turretMob.getRepairMaterial().name()),
                                turretMob.getRepairAmount()
                        )
                )
        );
    }

    private void removeRepairFromUser(InventoryClickEvent e) {
        InventoryModifyResult modifiedInventory = InventoryModify.withMaterial(getTurretMob().getRepairMaterial())
                .withActionPreCondition(InventoryModify.ActionPreCondition.ONLY_IF_COUNT_EXISTS)
                .withDesiredAmount(1)
                .withActionOnItems(InventoryModify.ActionOnItems.REMOVE_UNTIL_GOAL_REACHED)
                .runOnInventory(e.getWhoClicked().getInventory());
        if (modifiedInventory.countModified() != 0) {
            getTurretMob().repair(modifiedInventory.countModified());
        }
    }

    private void setBowSlot() {
        setSlot(new InventoryGuiSlotImplACD(e -> {
            @Nullable ItemStack itemThere = getTurretMob().removeBowSlot();
            if (itemThere != null)
                e.getWhoClicked().getInventory().addItem(itemThere);
        }, getTurretMob().getBowItem()), 40);
    }

    private void setArrowSlots() {
        List<ItemStack> arrows = getTurretMob().getArrowItems();
        for (int arrowIndex = 0; arrowIndex < 6; arrowIndex++) {
            ItemStack arrow = arrows.size() > arrowIndex ? arrows.get(arrowIndex) : null;
            int guiIndex = arrowIndex >= 3 ? arrowIndex + 6 : arrowIndex;
            if (arrow != null) {
                setArrowSlot(arrowIndex, guiIndex, arrow);
            } else {
                setSlot(new InventoryGuiSlotDoNothingACD(InventoryUtils.makeItem(Material.GLASS, "Arrow slot")), guiIndex);
            }
        }
    }

    private void setArrowSlot(int i, int guiIndex, ItemStack arrow) {
        setSlot(new InventoryGuiSlotImplACD((e) -> {
            ItemStack itemThere = getTurretMob().removeArrowSlot(i);
            if (itemThere != null)
                e.getWhoClicked().getInventory().addItem(itemThere);
        }, arrow), guiIndex);
    }

    @Override
    public void onPlayerInventory(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        Material material = item.getType();
        if (TurretUtils.isArrow(material)) {
            event.setCurrentItem(getTurretMob().addArrow(item));
        } else if (MaterialUtils.isBowLike(material)) {
            @Nullable ItemStack newItem = getTurretMob().addBow(item);
            event.setCurrentItem(newItem);
        }
    }

    public TurretMobGm getTurretMob() {
        return parent.getTurretMob();
    }

    @Override
    public String getName() {
        return "GM Turret";
    }

    @Override
    public int size() {
        return 54;
    }
}
