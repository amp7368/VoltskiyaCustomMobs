package apple.voltskiya.custom_mobs.leaps.hellish_catalyst;

import apple.voltskiya.custom_mobs.leaps.config.LeapPostConfig;
import apple.voltskiya.custom_mobs.leaps.config.LeapPreConfig;
import apple.voltskiya.custom_mobs.leaps.misc.PathfinderGoalLeap;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class PathfinderGoalLeapCatalyst extends PathfinderGoalLeap {
    /**
     * find a block to navigate to
     *
     * @param me         the entity to navigate
     * @param config     the config for the leap
     * @param postConfig provides any runtime info for the leap
     */
    public PathfinderGoalLeapCatalyst(EntityInsentient me, LeapPreConfig config, LeapPostConfig postConfig) {
        super(me, config, postConfig);
    }

    /**
     * @return a goal to jump to
     */
    @Nullable
    @Override
    protected Location getGoalLocation() {
        final Location location = this.me.getBukkitEntity().getLocation();
        Vector facing = location.getDirection();
        facing.setY(0);
        facing.normalize();
        facing.multiply(this.random.nextDouble() * config.getDistanceMin() + config.getDistanceMax() - config.getDistanceMin());

        int x = (int) (location.getX() + facing.getX());
        int y = (int) (location.getY() + facing.getY());
        int z = (int) (location.getZ() + facing.getZ());
        // find somewhere around here that has a reasonable y
        World world = location.getWorld();
        final int maxHeight = world.getMaxHeight();
        if (world.getBlockAt(x, y, z).getType().isAir()) {
            // check below for solid ground
            do {
                y--;
            } while (world.getBlockAt(x, y, z).getType().isAir() && y > 0);
        } else {
            do {
                y++;
            } while (!world.getBlockAt(x, y, z).getType().isAir() && y < maxHeight);
            // check above for air
        }
        location.set(x, y, z);
        return location;
    }
}
