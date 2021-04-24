package apple.voltskiya.custom_mobs.turrets.gui;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.turrets.TurretMob;
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

public class TurretGuiManager implements Listener {
    private static TurretGuiManager instance;
    private final Map<Long, TurretGui> turretGuis = new HashMap<>();

    public TurretGuiManager() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
    }

    public void open(Player player, TurretMob turret) {
        TurretGui gui = turretGuis.get(turret.getUniqueId());
        if (gui == null) {
            gui = new TurretGui(turret);
            synchronized (this) {
                this.turretGuis.put(turret.getUniqueId(), gui);
            }
            player.openInventory(gui.getInventory());
        }
    }

    public static TurretGuiManager get() {
        return instance;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryTurretGuiClick(InventoryClickEvent event) {
        final Inventory inventory = event.getClickedInventory();
        if (inventory == null) return;
        final InventoryHolder holder = inventory.getHolder();
        if (holder instanceof TurretGui) {
            synchronized (this) {
                ((TurretGui) holder).dealWithClick(event);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        final InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof TurretGui) {
            synchronized (this) {
                this.turretGuis.remove(((TurretGui) holder).getUniqueId());
            }
        }
    }

    public void updateGui(long turretUid) {
        TurretGui gui;
        synchronized (this) {
            gui = turretGuis.get(turretUid);
        }
        if (gui != null) {
            gui.updateView();
        }
    }
}