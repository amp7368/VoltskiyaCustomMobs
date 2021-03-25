package apple.voltskiya.custom_mobs.turrets;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TurretBuilder {
    private final Location location;
    private final List<UUID> turretEntities = new ArrayList<>();
    private UUID durabilityEntity;
    private UUID refilledEntity;
    private UUID bowEntity;

    /**
     * create a new TurretMob and register it as a new turret
     *
     * @param player the player who spawned the turret
     */
    public TurretBuilder(Player player) {
        this.location = player.getLocation();
    }

    public synchronized void addEntity(Entity e) {
        this.turretEntities.add(e.getUniqueId());
    }

    public synchronized void addDurabilityEntity(Entity e) {
        this.durabilityEntity = e.getUniqueId();
    }

    public synchronized void addRefilledEntity(Entity e) {
        this.refilledEntity = e.getUniqueId();
    }

    public synchronized void addBowEntity(Entity e) {
        this.bowEntity = e.getUniqueId();
    }

    public TurretMob build() {
        UUID world = location.getWorld().getUID();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        double facingX = location.getDirection().getX();
        double facingY = location.getDirection().getY();
        double facingZ = location.getDirection().getZ();
        return new TurretMob(world, x, y, z,
                facingX,
                facingY,
                facingZ,
                turretEntities,
                durabilityEntity,
                refilledEntity,
                bowEntity,
                TurretMob.MAX_HEALTH,
                new ArrayList<>(),
                null,
                0
        );
    }
}
