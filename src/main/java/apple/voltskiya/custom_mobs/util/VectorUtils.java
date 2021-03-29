package apple.voltskiya.custom_mobs.util;

import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class VectorUtils {
    @NotNull
    public static Vector rotateVector(double x1, double z1, double x2, double z2, double y, double theta) {
        double x2Old = x1 + x2;
        double z2Old = z1 + z2;
        // rotate these two points
        double x1New = x1 * Math.cos(theta) - z1 * Math.sin(theta);
        double z1New = z1 * Math.cos(theta) + x1 * Math.sin(theta);
        double x2New = x2Old * Math.cos(theta) - z2Old * Math.sin(theta);
        double z2New = z2Old * Math.cos(theta) + x2Old * Math.sin(theta);
        return new Vector(x2New - x1New, y, z2New - z1New);
    }
}
