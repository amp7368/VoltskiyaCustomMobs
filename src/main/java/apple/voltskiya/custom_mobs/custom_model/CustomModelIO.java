package apple.voltskiya.custom_mobs.custom_model;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.annotation.*;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@CommandAlias("model")
@CommandPermission("model.create")
public class CustomModelIO extends BaseCommand implements Listener {
    private final NamespacedKey guiNameKey = new NamespacedKey(VoltskiyaPlugin.get(), "custom_model_gui");

    public CustomModelIO() {
        VoltskiyaPlugin.get().getCommandManager().registerCommand(this);
        VoltskiyaPlugin.get().getCommandManager().getCommandCompletions().registerCompletion("model-schematics", CustomModelIO::getSchematics);
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
    }


    @Subcommand("gui")
    @CommandCompletion("optional|startingRadius optional|facingX optional|facingY optional|facingZ")
    public void model(Player player, @Optional Double radius, @Optional Double vx, @Optional Double vy, @Optional Double vz) {
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
                                        player.sendMessage(ChatColor.GREEN + "I saved that schematic under \"model\". Please rename it");
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

    @Subcommand("spawn")
    @CommandCompletion("@model-schematics")
    public void spawn(Player player, @Single String fileName) {
        Location location = player.getLocation();
        World world = location.getWorld();
        CustomModelData model = CustomModelPlugin.get().loadSchematic(fileName + ".yml");
        if (model != null) {
            List<CustomModelDataEntity> entities = model.entities;
            for (CustomModelDataEntity entity : entities) {
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
        player.sendMessage(ChatColor.GREEN + "Model spawned");
    }

    @Subcommand("modify")
    public class CustomModelIOModify extends BaseCommand {
        @Subcommand("rotate")
        @CommandCompletion("rotation @model-schematics")
        public void rotate(CommandSender commandSender, double rotation, @Single String fileName) {
            try {
                CustomModelPlugin.get().adjustSchematicRotateAll(rotation, fileName + ".yml");
                commandSender.sendMessage(ChatColor.GREEN + String.format("%s has been rotated by %f degrees", fileName, rotation));
            } catch (IOException e) {
                e.printStackTrace();
                commandSender.sendMessage(ChatColor.RED + String.format("There was an error rotating %s by %f degrees", fileName, rotation));
            } catch (IllegalArgumentException e) {
                commandSender.sendMessage(ChatColor.RED + e.getMessage());
            }
        }

        @Subcommand("offset xyz")
        @CommandCompletion("xOffset yOffset zOffset @model-schematics")
        public void offsetXYZ(CommandSender commandSender, double x, double y, double z, @Single String fileName) {
            CustomModelPlugin.get().adjustSchematicOffsetXYZ(x, y, z);
            commandSender.sendMessage(Color.GREEN + String.format("%s has been adjusted by %f, %f, %f", fileName, x, y, z));
        }

        @Subcommand("offset rotatePosition")
        @CommandCompletion("rotation @model-schematics")
        public void offsetRotate(CommandSender commandSender, double rotation, @Single String fileName) {
            try {
                CustomModelPlugin.get().adjustSchematicRotateXYZ(rotation, fileName + ".yml");
                commandSender.sendMessage(ChatColor.GREEN + String.format("%s has been rotated by %f degrees", fileName, rotation));
            } catch (IOException e) {
                e.printStackTrace();
                commandSender.sendMessage(ChatColor.RED + String.format("There was an error rotating %s by %f degrees", fileName, rotation));
            } catch (IllegalArgumentException e) {
                commandSender.sendMessage(ChatColor.RED + e.getMessage());
            }
        }
    }

    @Subcommand("schematic")
    public class CustomModelIOSchematic extends BaseCommand {
        @Subcommand("rename")
        @CommandCompletion("newName @model-schematics")
        public void renameSchematic(CommandSender sender, @Single String newName, @Single String oldName) {
            oldName = (oldName == null ? "model" : oldName) + ".yml";
            newName = newName + ".yml";
            final File folder = CustomModelPlugin.get().getDataFolder();
            File oldFile = new File(folder, oldName);
            oldFile.renameTo(new File(folder, newName));
            sender.sendMessage(oldName + " --> " + newName);
        }
    }


    public static Collection<String> getSchematics(BukkitCommandCompletionContext context) {
        File folder = CustomModelPlugin.get().getDataFolder();
        return Arrays.stream(folder.list((f, f2) -> f2.endsWith(".yml"))).map(fn -> fn.substring(0, fn.length() - 4)).collect(Collectors.toList());
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