package apple.voltskiya.custom_mobs.util.minecraft;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import static org.bukkit.Material.*;

public class MaterialUtils {
    private final static Collection<Material> arrows = Arrays.asList(
            ARROW,
            SPECTRAL_ARROW,
            TIPPED_ARROW
    );
    private static final Collection<Material> walkThroughable = new HashSet<>(Arrays.asList(
            SNOW,
            GRASS,
            FERN,
            FLOWER_POT,
            TALL_GRASS,
            LARGE_FERN,
            BIRCH_BUTTON,
            ACACIA_BUTTON,
            DARK_OAK_BUTTON,
            CRIMSON_BUTTON,
            JUNGLE_BUTTON,
            SPRUCE_BUTTON,
            OAK_BUTTON,
            POLISHED_BLACKSTONE_BUTTON,
            STONE_BUTTON

    ));

    public static boolean isArrow(Material m) {
        return arrows.contains(m);
    }

    public static boolean isBowLike(Material m) {
        return BOW == m || CROSSBOW == m;
    }

    public static boolean isPassable(Material m) {
        return m.isAir() || m == SNOW;
    }

    public static boolean isWalkThroughable(Material m) {
        return m.isAir() || walkThroughable.contains(m);
    }
}
