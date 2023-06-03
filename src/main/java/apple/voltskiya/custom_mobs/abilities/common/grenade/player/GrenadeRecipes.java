package apple.voltskiya.custom_mobs.abilities.common.grenade.player;

import apple.mc.utilities.inventory.item.InventoryUtils;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftShapedRecipe;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GrenadeRecipes {

    public static final String GRENADE_BOMB = "grenade.bomb";
    public static final Set<String> ALL_GRENADES = Set.of(GRENADE_BOMB);

    public static void load() {
        CraftShapedRecipe recipe = grenadeRecipe();
        Bukkit.addRecipe(recipe);
    }

    @NotNull
    private static CraftShapedRecipe grenadeRecipe() {
        ItemStack result = new ItemStack(Material.OAK_LOG);
        InventoryUtils.get().setItemFlags(result, GRENADE_BOMB);
        CraftShapedRecipe recipe = new CraftShapedRecipe(VoltskiyaPlugin.get().namespacedKey(GRENADE_BOMB), result);
        recipe.shape("g g", " g ", "g g");
        recipe.setIngredient('g', Material.BEDROCK);
        return recipe;
    }
}
