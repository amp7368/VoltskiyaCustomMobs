package apple.voltskiya.custom_mobs.util.minecraft;

public class EnchantmentUtils {
    public static boolean randomBreakUnbreaking(int unbreakingLevel) {
        return Math.random() <= 1f / (unbreakingLevel + 1);
    }

    public static double damage(double baseDamage, int powerLevel) {
        return baseDamage + 0.5 * powerLevel;
    }

    public static int knockback(int enchantmentLevel) {
        return enchantmentLevel;
    }

    public static int flame(int enchantmentLevel) {
        return enchantmentLevel > 0 ? Integer.MAX_VALUE : 0;
    }
}
