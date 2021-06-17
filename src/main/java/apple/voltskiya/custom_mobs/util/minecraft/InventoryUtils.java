package apple.voltskiya.custom_mobs.util.minecraft;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InventoryUtils {
    public static ItemStack makeItem(Material material, int amount, @Nullable String name, @Nullable List<String> lore) {
        final ItemStack item = new ItemStack(material, amount);

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setLore(lore);
        if (name != null)
            itemMeta.setDisplayName(name);
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack makeItem(Material material, int amount, @Nullable BaseComponent[] name, @Nullable List<String> lore) {
        final ItemStack item = new ItemStack(material, amount);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setLore(lore);
        if (name != null)
            itemMeta.setDisplayNameComponent(name);
        item.setItemMeta(itemMeta);
        return item;
    }
}
