package apple.voltskiya.custom_mobs.mobs.parts;

import apple.voltskiya.custom_mobs.util.EntityLocation;
import net.minecraft.server.v1_16_R3.Entity;
import org.bukkit.Location;

public class MobPartMother {
    public final Location location;
    public final EntityLocation entityLocation;
    public final Entity entity;

    public MobPartMother(EntityLocation entityLocation, Entity entity) {
        this.entityLocation = entityLocation;
        this.entity = entity;
        this.location = new Location(null, entityLocation.x, entityLocation.y, entityLocation.z);
    }
}
