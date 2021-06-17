package apple.voltskiya.custom_mobs.gui;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class InventoryGuiListener implements Listener {
    public InventoryGuiListener() {
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryTurretGuiClick(InventoryClickEvent event) {
        final Inventory clickedInventory = event.getClickedInventory();
        if ((clickedInventory != null && clickedInventory.getHolder() instanceof InventoryGui)) {
            // clicking from the turret
            ((InventoryGui) clickedInventory.getHolder()).onGuiInventory(event);
        } else {
            final InventoryHolder topInventory = event.getView().getTopInventory().getHolder();
            if (topInventory instanceof InventoryGui) {
                ((InventoryGui) topInventory).onPlayerInventory(event);
            }
        }
    }
}
