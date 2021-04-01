package apple.voltskiya.custom_mobs.custom_model;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
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
    @CommandCompletion("facingX facingY facingZ startingRadius")
    public void model(Player player, @Optional Double vx, @Optional Double vy, @Optional Double vz, @Optional Double radius) {
        if (vx == null || vy == null || vz == null) {
            vx = 1d;
            vy = 0d;
            vz = 0d;
        }
        if (Math.abs(vx) < 0.01 && Math.abs(vy) < 0.01 && Math.abs(vz) < 0.01) {
            player.sendMessage(ChatColor.RED + "You should use a vector that's not equal to zero.");
            return;
        }
        @NotNull ItemStack item = new ItemStack(Material.STICK);
        ItemMeta im = item.getItemMeta();
        im.getPersistentDataContainer().set(guiNameKey,
                PersistentDataType.STRING,
                CustomModelGuiList.put(
                        new CustomModelGui(player, new Vector(vx, vy, vz), radius,
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

    @Subcommand("spawn")
    public void spawn(Player player) {
        Location location = player.getLocation();
        World world = location.getWorld();
        CustomModel model = CustomModelPlugin.get().loadSchematic();
        if (model != null) {
            List<CustomModel.CustomEntity> entities = model.entities;
            for (CustomModel.CustomEntity entity : entities) {
                world.spawnEntity(location.clone().add(entity.x, entity.y, entity.z).setDirection(
                        new Vector(
                                entity.facingX,
                                entity.facingY,
                                entity.facingZ
                        )
                ), entity.type, CreatureSpawnEvent.SpawnReason.CUSTOM, spawned -> {
                    Location loc = spawned.getLocation();
                    ((CraftEntity) spawned).getHandle().load(entity.nbt);
                    spawned.teleport(loc);
                });
            }
        }
    }

    @Subcommand("rotate")
    public void rotate(double rotation) {
        try {
            CustomModelPlugin.get().rotate(rotation);
        } catch (IOException e) {
            e.printStackTrace();
        }
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