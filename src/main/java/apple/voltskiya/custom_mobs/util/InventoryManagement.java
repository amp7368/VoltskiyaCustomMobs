package apple.voltskiya.custom_mobs.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InventoryManagement {
    public static ItemStack makeItem(Material material, int amount, @Nullable String name, @Nullable List<String> lore) {
        final ItemStack item = new ItemStack(material, amount);

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setLore(lore);
        if (name != null)
            itemMeta.setDisplayName(name);
        item.setItemMeta(itemMeta);
        return item;
    }
}
