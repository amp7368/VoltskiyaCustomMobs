package apple.voltskiya.custom_mobs.trash.dungeon.patrols;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.trash.dungeon.product.DungeonActive;
import apple.voltskiya.custom_mobs.trash.dungeon.scanned.DungeonScanned;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PatrolWandListener implements Listener {
    public PatrolWandListener() {
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
    }

    @EventHandler
    public void onWandUse(PlayerInteractEvent event) {
        final ItemStack item = event.getItem();
        if (item != null) {
            PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
            if (data.has(PatrolWand.PATROL_WAND_NAMESPACE, PersistentDataType.STRING)) {
                String wand = data.get(PatrolWand.PATROL_WAND_NAMESPACE, PersistentDataType.STRING);
                if (wand != null) {
                    String[] wandSplit = wand.split(":");
                    if (wandSplit.length == 2) {
                        final DungeonScanned dungeon = DungeonActive.getDungeon(wandSplit[0]).getScanned();
                        if (dungeon != null) {
                            Patrol patrol = dungeon.getPatrol(wandSplit[1]);
                            if (patrol != null) {
                                if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                                    final Block clickedBlock = event.getClickedBlock();
                                    if (clickedBlock != null) {
                                        patrol.addStep(clickedBlock);
                                        final Location l = clickedBlock.getLocation();
                                        event.getPlayer().sendMessage(ChatColor.AQUA + String.format("Added step at [%d, %d, %d]", l.getBlockX(), l.getBlockY(), l.getBlockZ()));
                                    }
                                } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                    patrol.openGui(event.getPlayer());
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
