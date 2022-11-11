package apple.voltskiya.custom_mobs.nms.cool.aledar.mob;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

@CommandAlias("aledar")
public class AledarNavigation extends BaseCommand implements Listener {
    public static final NamespacedKey ALEDAR_KEY = new NamespacedKey(VoltskiyaPlugin.get(), "aledar");
    private static double SPEED = 1.1;

    public AledarNavigation() {
        VoltskiyaPlugin.get().getCommandManager().registerCommand(this);
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
}
