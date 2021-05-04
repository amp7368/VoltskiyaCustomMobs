package apple.voltskiya.custom_mobs.util.minecraft;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.Collection;

public class MaterialUtils {
    private final static Collection<Material> arrows = Arrays.asList(
            Material.ARROW,
            Material.SPECTRAL_ARROW,
            Material.TIPPED_ARROW
    );

    public static boolean isArrow(Material m) {
        return arrows.contains(m);
    }

    public static boolean isBowLike(Material m) {
        return Material.BOW == m || Material.CROSSBOW == m;
    }
}
