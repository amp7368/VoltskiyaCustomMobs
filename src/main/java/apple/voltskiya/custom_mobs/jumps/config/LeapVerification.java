package apple.voltskiya.custom_mobs.jumps.config;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.List;

public class LeapVerification {
    /**
     * gets all the possible leap ranges that are reasonable to calculate
     *
     * @param currentLocation the starting location for a leap
     * @param leapConfig      the config containing constraints for possible leaps
     * @param minVector       the starting vector to look at
     * @param angle           the angle in the x-z plane to the new vector
     * @return
     */
    public List<LeapRange> verifyLeap(Location currentLocation, LeapConfig leapConfig, Vector minVector, double angle) {
        World world = currentLocation.getWorld();

        return null;
    }
}
