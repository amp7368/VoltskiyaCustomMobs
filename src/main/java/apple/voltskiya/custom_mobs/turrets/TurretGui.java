package apple.voltskiya.custom_mobs.turrets;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class TurretGui implements InventoryHolder {
    @NotNull Inventory inventory;

    public TurretGui(TurretMob turret) {
        this.inventory = Bukkit.createInventory(this, 54);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
