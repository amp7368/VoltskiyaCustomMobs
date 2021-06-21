package apple.voltskiya.custom_mobs.mobs.testing.aledar;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import net.minecraft.world.entity.monster.EntityPillager;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;

import java.util.Collection;

@CommandAlias("aledar")
public class AledarNavigation extends BaseCommand implements Listener {
    public static final NamespacedKey ALEDAR_KEY = new NamespacedKey(VoltskiyaPlugin.get(), "aledar");
    private static double SPEED = 1.1;

    public AledarNavigation() {
        VoltskiyaPlugin.get().getCommandManager().registerCommand(this);
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
    }

    @Subcommand("here")
    public void here(Player player) {
        ItemStack wand = new ItemStack(Material.STICK);
        ItemMeta meta = wand.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(ALEDAR_KEY, PersistentDataType.BYTE, (byte) 1);
        wand.setItemMeta(meta);
        player.getInventory().addItem(wand);
    }

    @Subcommand("speed")
    @CommandCompletion("newSpeed")
    public void speed(double newSpeed) {
        SPEED = newSpeed;
    }

    @EventHandler
    public void onWand(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() ==Action.LEFT_CLICK_AIR) {
            ItemStack item = event.getItem();
            if (item != null) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    Byte isAledar = meta.getPersistentDataContainer().get(ALEDAR_KEY, PersistentDataType.BYTE);
                    if (isAledar != null && isAledar == 1) {
                        // do the thing and make all nearby aledar's come here
                        RayTraceResult block = event.getPlayer().rayTraceBlocks(50, FluidCollisionMode.ALWAYS);
                        if (block != null) {
                            Block blockClicked = block.getHitBlock();
                            if (blockClicked != null) {
                                Collection<Entity> nearby = blockClicked.getLocation().getNearbyEntities(50, 50, 50);
                                for (Entity e : nearby) {
                                    final net.minecraft.world.entity.Entity entity = ((CraftEntity) e).getHandle();
                                    if (entity.getEntityType() == MobAledar.mobAledarEntityType) {
                                        // this is aledar
                                        EntityPillager aledar = (EntityPillager) entity;
                                        aledar.getNavigation().o();
                                        aledar.getNavigation().a(blockClicked.getX(), blockClicked.getY(), blockClicked.getZ(), SPEED);
                                    }
                                }
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }
}
