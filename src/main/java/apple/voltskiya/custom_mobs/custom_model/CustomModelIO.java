package apple.voltskiya.custom_mobs.custom_model;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.UUID;

@CommandAlias("model")
@CommandPermission("model.create")
public class CustomModelIO extends BaseCommand implements Listener {
    private final NamespacedKey guiNameKey = new NamespacedKey(VoltskiyaPlugin.get(), "custom_model_gui");

    public CustomModelIO() {
        VoltskiyaPlugin.get().getCommandManager().registerCommand(this);
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
    }

    @Default
    public void model(Player player, double vx, double vy, double vz) {
        if (Math.abs(vx) < 0.01 && Math.abs(vy) < 0.01 && Math.abs(vz) < 0.01) {
            player.sendMessage(ChatColor.RED + "You should use a vector that's not equal to zero.");
        }
        @NotNull ItemStack item = new ItemStack(Material.STICK);
        ItemMeta im = item.getItemMeta();
        im.getPersistentDataContainer().set(guiNameKey,
                PersistentDataType.STRING,
                CustomModelGuiList.put(
                        new CustomModelGui(player,new Vector(vx,vy,vz),
                                (gui) -> {
                                    try {
                                        CustomModelPlugin.get().saveSchematic(gui);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                        )
                ).toString());
        im.setLocalizedName("Modeling tool");
        item.setItemMeta(im);
        player.getInventory().addItem(item);
    }

    @Subcommand("offset")
    public void offset(double x, double y, double z) {
        CustomModelPlugin.get().adjustSchematic(x, y, z);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof CustomModelGui) {
            ((CustomModelGui) event.getInventory().getHolder()).click(event);
        }
    }

    @EventHandler
    public void openOrExecute(PlayerInteractEvent event) {
        @Nullable ItemStack mainHand = event.getItem();
        if (mainHand != null) {
            ItemMeta meta = mainHand.getItemMeta();
            if (meta == null) return;
            if (meta.getPersistentDataContainer().has(guiNameKey, PersistentDataType.STRING)) {
                String gui = meta.getPersistentDataContainer().get(guiNameKey, PersistentDataType.STRING);
                if (gui != null) {
                    UUID uuid = null;
                    try {
                        uuid = UUID.fromString(gui);
                    } catch (IllegalArgumentException ignored) {
                    }
                    if (uuid != null) {
                        CustomModelGui modelGui = CustomModelGuiList.get(uuid);
                        if (modelGui != null) {
                            if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                                event.getPlayer().openInventory(modelGui.getInventory());
                            } else {
                                modelGui.execute();
                            }
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
}