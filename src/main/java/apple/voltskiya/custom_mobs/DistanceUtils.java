package apple.voltskiya.custom_mobs;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class DistanceUtils {
    public static double distance(Location aLocation, Location bLocation) {
        if (aLocation.getWorld().getUID() != bLocation.getWorld().getUID()) {
            return Double.MAX_VALUE;
        }
        int aX = aLocation.getBlockX();
        int aY = aLocation.getBlockY();
        int aZ = aLocation.getBlockZ();
        int bX = bLocation.getBlockX();
        int bY = bLocation.getBlockY();
        int bZ = bLocation.getBlockZ();
        int dx = aX - bX;
        int dy = aY - bY;
        int dz = aZ - bZ;
        return magnitude(dx, dy, dz);
    }

    public static double magnitude(Location l) {
        return magnitude(l.getX(), l.getY(), l.getZ());
    }

    public static double magnitude(Vector l) {
        return magnitude(l.getX(), l.getY(), l.getZ());
    }

    public static double magnitude(double x, double y, double z) {
        return Math.sqrt(x * x + y * y + z * z); // a^2 + b^2 = c^2
    }

    @Nullable
    public static Vector unitVector(Vector v) {
        double magnitude = magnitude(v);
        if (magnitude > 0.1) {
            return v.clone().divide(new Vector(magnitude, magnitude, magnitude));
        }
        return null;
    }
}
