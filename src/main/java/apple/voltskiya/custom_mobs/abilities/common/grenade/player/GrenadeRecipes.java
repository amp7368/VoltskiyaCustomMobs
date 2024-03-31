package apple.voltskiya.custom_mobs.abilities.common.grenade.player;

import apple.mc.utilities.inventory.item.InventoryUtils;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftShapedRecipe;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GrenadeRecipes {

    public static final String GRENADE_FLASHBANG = "grenade.flashbang";
    public static final String GRENADE_BOMB = "grenade.bomb";
    public static final Set<String> ALL_GRENADES = Set.of(GRENADE_FLASHBANG, GRENADE_BOMB);

    public static void load() {
        Bukkit.addRecipe(grenadeRecipe());
        Bukkit.addRecipe(flashbangRecipe());
    }

    @NotNull
    private static CraftShapedRecipe flashbangRecipe() {
        ItemStack result = InventoryUtils.get().makeItem(Material.OAK_LOG, "Flashbang");
        InventoryUtils.get().setItemFlags(result, GRENADE_FLASHBANG);
        CraftShapedRecipe recipe = new CraftShapedRecipe(VoltskiyaPlugin.get().namespacedKey(GRENADE_FLASHBANG), result);
        recipe.shape("   ", "ggg", "   ");
        recipe.setIngredient('g', Material.BEDROCK);
        return recipe;
    }

    @NotNull
    private static CraftShapedRecipe grenadeRecipe() {
        ItemStack result = InventoryUtils.get().makeItem(Material.OAK_LOG, "Grenade");
        InventoryUtils.get().setItemFlags(result, GRENADE_BOMB);
        CraftShapedRecipe recipe = new CraftShapedRecipe(VoltskiyaPlugin.get().namespacedKey(GRENADE_BOMB), result);
        recipe.shape("g g", " g ", "g g");
        recipe.setIngredient('g', Material.BEDROCK);
        return recipe;
    }
}
