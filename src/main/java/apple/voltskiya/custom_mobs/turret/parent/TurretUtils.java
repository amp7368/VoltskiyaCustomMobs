package apple.voltskiya.custom_mobs.turret.parent;

import org.bukkit.Material;
import voltskiya.apple.utilities.util.minecraft.MaterialUtils;

public class TurretUtils {
    public static boolean isArrow(Material material) {
        return MaterialUtils.isArrow(material) || material == Material.EGG || material == Material.SNOWBALL;
    }
}
