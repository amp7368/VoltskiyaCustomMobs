package apple.voltskiya.custom_mobs.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public static Vector rotateVector(double facingX, double facingZ, double facingY, double rotation) {
        double angleStarting = Math.atan2(facingZ, facingX);
        angleStarting += rotation;
        return new Vector(Math.cos(angleStarting), facingY, Math.sin(angleStarting));
    }

    /**
     * rotates the entity about a center
     *
     * @param entityLocation the location to rotate
     * @param yaw            the new direction for entityLocation to face
     * @param center         the center to rotate about
     * @param isModifyEntity whether to modify the specified entity's location
     * @return the new rotated loaction
     */
    public static @NotNull Location rotate(EntityLocation entityLocation, float yaw, Location center, boolean isModifyEntity) {
        Location l = new Location(null, 0, 0, 0, yaw, 0);
        return rotate(entityLocation, l.getDirection(), center, isModifyEntity);
    }

    /**
     * rotates the entity about a center
     *
     * @param entityLocation the location to rotate
     * @param newFacing      the new direction for entityLocation to face
     * @param center         the center to rotate about
     * @param isModifyEntity whether to modify the specified entity's location
     * @return the new rotated loaction
     */
    public static @NotNull Location rotate(EntityLocation entityLocation, Vector newFacing, Location center, boolean isModifyEntity) {
        double radius = DistanceUtils.magnitude(
                entityLocation.x,
                0,
                entityLocation.z);

        // do the position rotation
        double angle = Math.atan2(entityLocation.z, entityLocation.x);
        angle += Math.atan2(newFacing.getZ(), newFacing.getX());
        double x = Math.cos(angle) * radius + center.getX();
        double z = Math.sin(angle) * radius + center.getZ();

        // do the facing rotation
        double theta = Math.atan2(newFacing.getZ(), newFacing.getX()) ; // todo make this 0
        while (theta < 0) theta += Math.PI * 2;
        Vector newEntityFacing = rotateVector(entityLocation.x, entityLocation.z, entityLocation.xFacing, entityLocation.zFacing, entityLocation.yFacing, theta);
        Location newLocation = new Location(null, x, entityLocation.y, z);
        newLocation.setDirection(newEntityFacing);

        @Nullable Entity entity = Bukkit.getEntity(entityLocation.uuid);
        if (entity != null) {
            Location changeLocation = entity.getLocation().setDirection(newEntityFacing);
            changeLocation.setX(x);
            changeLocation.setZ(z);
            if (isModifyEntity) entity.teleport(changeLocation);
        }

        return newLocation;
    }
}
