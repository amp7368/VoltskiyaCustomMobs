package apple.voltskiya.custom_mobs.turrets;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TurretBuilder {
    private final Location location;
    private final List<EntityLocation> turretEntities = new ArrayList<>();
    private EntityLocation durabilityEntity;
    private Entity durabilityEntityReal;
    private EntityLocation refilledEntity;
    private EntityLocation bowEntity;

    /**
     * create a new TurretMob and register it as a new turret
     *
     * @param player the player who spawned the turret
     */
    public TurretBuilder(Player player) {
        this.location = player.getLocation();
    }

    public synchronized void addEntity(Entity e) {
        this.turretEntities.add(new EntityLocation(e));
        e.addScoreboardTag(TurretMob.TURRET_TAG);
    }

    public synchronized void addDurabilityEntity(Entity e) {
        this.durabilityEntity = new EntityLocation(e);
        this.durabilityEntityReal = e;
        e.addScoreboardTag(TurretMob.TURRET_TAG);
    }

    public synchronized void addRefilledEntity(Entity e) {
        this.refilledEntity = new EntityLocation(e);
        e.addScoreboardTag(TurretMob.TURRET_TAG);
    }

    public synchronized void addBowEntity(Entity e) {
        this.bowEntity = new EntityLocation(e);
        e.addScoreboardTag(TurretMob.TURRET_TAG);
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
                durabilityEntityReal,
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
