package apple.voltskiya.custom_mobs.turrets;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.UUID;

public class EntityLocation {
    public UUID uuid;
    public double x;
    public double y;
    public double z;

    public EntityLocation(UUID uuid, double x, double y, double z) {
        this.uuid = uuid;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public EntityLocation(Entity e) {
        this.uuid = e.getUniqueId();
        final Location location = e.getLocation();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
    }
}
