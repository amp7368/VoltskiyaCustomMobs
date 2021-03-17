package apple.voltskiya.custom_mobs;

import org.bukkit.Location;

public class DistanceUtils {
    public static double distance(Location aLocation, Location bLocation) {
        int aX = aLocation.getBlockX();
        int aY = aLocation.getBlockY();
        int aZ = aLocation.getBlockZ();
        int bX = bLocation.getBlockX();
        int bY = bLocation.getBlockY();
        int bZ = bLocation.getBlockZ();
        int dx = aX - bX;
        int dy = aY - bY;
        int dz = aZ - bZ;
        return Math.sqrt(dx * dx + dy * dy + dz * dz); // a^2 + b^2 = c^2
    }
}
