package apple.voltskiya.custom_mobs.util.minecraft;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import static org.bukkit.Material.*;

public class MaterialUtils {
    private final static Collection<Material> ARROWS = Arrays.asList(
            ARROW,
            SPECTRAL_ARROW,
            TIPPED_ARROW
    );
    private final static Collection<Material> BUTTONS = Arrays.asList(
            BIRCH_BUTTON,
            ACACIA_BUTTON,
            DARK_OAK_BUTTON,
            JUNGLE_BUTTON,
            SPRUCE_BUTTON,
            OAK_BUTTON,
            WARPED_BUTTON,
            CRIMSON_BUTTON,
            POLISHED_BLACKSTONE_BUTTON,
            STONE_BUTTON
    );
    private final static Collection<Material> TRAP_DOORS = Arrays.asList(
            BIRCH_TRAPDOOR,
            ACACIA_TRAPDOOR,
            DARK_OAK_TRAPDOOR,
            JUNGLE_TRAPDOOR,
            SPRUCE_TRAPDOOR,
            OAK_TRAPDOOR,
            WARPED_TRAPDOOR,
            CRIMSON_TRAPDOOR,
            IRON_TRAPDOOR
    );
    private final static Collection<Material> CARPETS = Arrays.asList(
            WHITE_CARPET,
            ORANGE_CARPET,
            MAGENTA_CARPET,
            LIGHT_BLUE_CARPET,
            YELLOW_CARPET,
            LIME_CARPET,
            PINK_CARPET,
            GRAY_CARPET,
            LIGHT_GRAY_CARPET,
            CYAN_CARPET,
            PURPLE_CARPET,
            BLUE_CARPET,
            BROWN_CARPET,
            GREEN_CARPET,
            RED_CARPET,
            BLACK_CARPET
    );
    private static final Collection<Material> walkThroughable = new HashSet<>(Arrays.asList(
            SNOW,
            GRASS,
            FERN,
            FLOWER_POT,
            TALL_GRASS,
            LARGE_FERN,
            REDSTONE,
            STRING
    )) {{
        addAll(TRAP_DOORS);
        addAll(BUTTONS);
        addAll(CARPETS);
    }};

    public static boolean isArrow(Material m) {
        return ARROWS.contains(m);
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
