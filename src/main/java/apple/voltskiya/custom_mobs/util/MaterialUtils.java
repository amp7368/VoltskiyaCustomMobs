package apple.voltskiya.custom_mobs.util;

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
}
