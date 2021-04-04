package apple.voltskiya.custom_mobs.turrets;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TurretGui implements InventoryHolder {
    @NotNull Inventory inventory;

    public TurretGui(TurretMob turret) {
        this.inventory = Bukkit.createInventory(this, 54);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
    private enum InventorySelection{
        ;
        private final int index;
        private final Consumer<InventoryClickEvent> dealWithClick;

        InventorySelection(int index, Consumer<InventoryClickEvent> dealWithClick) {
            this.index = index;
            this.dealWithClick = dealWithClick;
//            slotToAction.put(index, this);
        }

//        public static void dealWithClick(int slot, @NotNull HumanEntity player, CustomModelGui gui) {
//            CustomModelGui.Page1.InventorySelection action = slotToAction.get(slot);
//            if (action != null) {
//                gui.action = () -> action.dealWithClick.accept(player, gui);
//                gui.action.run();
//            }
//        }
    }
}
