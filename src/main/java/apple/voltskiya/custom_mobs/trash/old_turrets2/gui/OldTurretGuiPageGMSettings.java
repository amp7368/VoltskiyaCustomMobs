package apple.voltskiya.custom_mobs.trash.old_turrets2.gui;

import apple.voltskiya.custom_mobs.trash.old_turrets2.mobs.Old2TurretMobGM;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.utilities.util.gui.InventoryGui;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotGeneric;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.IntSupplier;

public class OldTurretGuiPageGMSettings extends OldTurretGuipageInfiniteSettings {
    private final Old2TurretMobGM turretMob;

    public OldTurretGuiPageGMSettings(Old2TurretMobGM turretMob, OldTurretGuiGM turretGui) {
        super(turretMob, turretGui);
        this.turretMob = turretMob;
    }

    @Override
    protected void setSlots() {
        super.setSlots();
        setSlot(new InventoryGuiSlotGeneric(
                e -> {
                    ClickType click = e.getClick();
                    if (click.isShiftClick()) {
                        turretMob.incrementDamage(click.isLeftClick() ? .1 : -.1);
                    } else {
                        turretMob.incrementDamage(click.isLeftClick() ? 1 : -1);
                    }
                    update();
                },
                InventoryUtils.makeItem(Material.RED_GLAZED_TERRACOTTA, 1, String.format("Damage %.1f", turretMob.getDamage()), Arrays.asList(
                        "Shift Left click - increment by 0.1",
                        "Shift Right click - decrement by -0.1",
                        "Left click - increment by 1",
                        "Right click - decrement by -1"
                ))
        ), 26);
        setSlot(new DisplaySlot(1, turretMob::getFlame, turretMob::incrementFlame, Material.CAMPFIRE, "Flame"), 25);
        setSlot(new DisplaySlot(1, turretMob::getKnockback, turretMob::incrementKnockback, Material.OAK_SIGN, "Knockback"), 34);
        setSlot(new DisplaySlot(1, turretMob::getPierceLevel, turretMob::incrementPierceLevel, Material.ARROW, "Pierce Level"), 35);
    }

    private class DisplaySlot implements InventoryGui.InventoryGuiSlot {
        private final int normalClick;
        private final IntSupplier supplier;
        private final Consumer<Integer> incrementer;
        private final Material material;
        private final String name;

        public DisplaySlot(int normalClick, IntSupplier supplier, Consumer<Integer> incrementer, Material material, String name) {
            this.normalClick = normalClick;
            this.supplier = supplier;
            this.incrementer = incrementer;
            this.material = material;
            this.name = name;
        }

        @Override
        public void dealWithClick(InventoryClickEvent inventoryClickEvent) {
            if (inventoryClickEvent.getClick().isLeftClick()) {
                incrementer.accept(normalClick);
            } else {
                incrementer.accept(-normalClick);
            }
            update();
        }

        @Override
        public ItemStack getItem() {
            return InventoryUtils.makeItem(material, 1, String.format("%s - %d", name, supplier.getAsInt()), Arrays.asList(
                    "Left click - increment by " + normalClick,
                    "Right click - decrement by -" + normalClick
            ));
        }
    }
}
