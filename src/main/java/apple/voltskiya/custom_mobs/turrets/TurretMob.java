package apple.voltskiya.custom_mobs.turrets;

import apple.voltskiya.custom_mobs.sql.TurretsSql;
import apple.voltskiya.custom_mobs.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class TurretMob implements Runnable {
    public static final String TURRET_TAG = "player.turret";
    public static final String TURRET_UID = "TurretUid";
    protected static final int MAX_HEALTH = 200;
    private final Location center;
    private final List<UUID> turretEntities;
    private final UUID durabilityEntity;
    private final UUID refilledEntity;
    private final UUID bowEntity;
    private final List<Pair<Material, Integer>> arrows;
    private final Material bow;
    private final int bowDurability;
    private double health;
    private long uid;


    public TurretMob(UUID worldUid, double x, double y, double z,
                     double facingX, double facingY, double facingZ,
                     List<UUID> turretEntities,
                     UUID durabilityEntity, UUID refilledEntity, UUID bowEntity,
                     double health,
                     List<Pair<Material, Integer>> arrows,
                     Material bow,
                     int bowDurability
    ) {
        final World world = Bukkit.getWorld(worldUid);
        this.center = new Location(world, x, y, z);
        this.center.setDirection(new Vector(facingX, facingY, facingZ));
        this.turretEntities = turretEntities;
        this.durabilityEntity = durabilityEntity;
        this.refilledEntity = refilledEntity;
        this.bowEntity = bowEntity;
        this.health = health;
        this.arrows = arrows;
        this.bow = bow;
        this.bowDurability = bowDurability;
        this.uid = -1;
    }

    public TurretMob(UUID worldUid, double x, double y, double z,
                     double facingX, double facingY, double facingZ,
                     List<UUID> turretEntities,
                     UUID durabilityEntity, UUID refilledEntity, UUID bowEntity,
                     double health,
                     List<Pair<Material, Integer>> arrows,
                     Material bow,
                     int bowDurability,
                     int uid
    ) {
        final World world = Bukkit.getWorld(worldUid);
        this.center = new Location(world, x, y, z);
        this.center.setDirection(new Vector(facingX, facingY, facingZ));
        this.turretEntities = turretEntities;
        this.durabilityEntity = durabilityEntity;
        this.refilledEntity = refilledEntity;
        this.bowEntity = bowEntity;
        this.health = health;
        this.arrows = arrows;
        this.bow = bow;
        this.bowDurability = bowDurability;
        this.uid = uid;
    }

    public synchronized void damage(double damage) {
        this.health -= damage;
        new Thread(this).start();
    }

    /**
     * update the database
     */
    @Override
    public void run() {
        try {
            TurretsSql.registerOrUpdate(this);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public Location getCenter() {
        return center;
    }

    public List<UUID> getTurretEntities() {
        return turretEntities;
    }

    public UUID getDurabilityEntity() {
        return durabilityEntity;
    }

    public UUID getRefilledEntity() {
        return refilledEntity;
    }

    public UUID getBowEntity() {
        return bowEntity;
    }

    public List<Pair<Material, Integer>> getArrows() {
        return arrows;
    }

    public Material getBow() {
        return bow;
    }

    public int getBowDurability() {
        return bowDurability;
    }

    public double getHealth() {
        return health;
    }

    public long getUniqueId() {
        return uid;
    }
}
