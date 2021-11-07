package apple.voltskiya.custom_mobs.trash.old_turrets.gui;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.trash.old_turrets.OldTurretMob;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;

public class OldTurretGuiManager implements Listener {
    private static OldTurretGuiManager instance;
    private final Map<Long, OldTurretGui> turretGuis = new HashMap<>();

    public OldTurretGuiManager() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
    }

    public static OldTurretGuiManager get() {
        return instance;
    }

    public void open(Player player, OldTurretMob turret) {
        OldTurretGui gui = turretGuis.get(turret.getUniqueId());
        if (gui == null) {
            gui = new OldTurretGui(turret);
            synchronized (this) {
                this.turretGuis.put(turret.getUniqueId(), gui);
            }
        }
        player.openInventory(gui.getInventory());
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryTurretGuiClick(InventoryClickEvent event) {
        final Inventory clickedInventory = event.getClickedInventory();
        if ((clickedInventory != null && clickedInventory.getHolder() instanceof OldTurretGui)) {
            // clicking from the turret
            ((OldTurretGui) clickedInventory.getHolder()).toPlayerInventory(event);
        } else {
            final InventoryHolder topInventory = event.getView().getTopInventory().getHolder();
            if (topInventory instanceof OldTurretGui) {
                ((OldTurretGui) topInventory).toTurretInventory(event);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
            final InventoryHolder holder = event.getInventory().getHolder();
            if (holder instanceof OldTurretGui) {
                synchronized (this) {
                    this.turretGuis.remove(((OldTurretGui) holder).getUniqueId());
                }
            }
        }, 1);
    }

    public void updateGui(long turretUid) {
        OldTurretGui gui;
        synchronized (this) {
            gui = turretGuis.get(turretUid);
        }
        if (gui != null) {
            gui.updateView();
        }
    }
}
