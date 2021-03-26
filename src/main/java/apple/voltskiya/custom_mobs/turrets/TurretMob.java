package apple.voltskiya.custom_mobs.turrets;

import apple.voltskiya.custom_mobs.sql.TurretsSql;
import apple.voltskiya.custom_mobs.util.DistanceUtils;
import apple.voltskiya.custom_mobs.util.Pair;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class TurretMob implements Runnable {
    public static final String TURRET_TAG = "player.turret";
    private final static int MAX_SIGHT = 50;
    protected static final int MAX_HEALTH = 200;
    protected static final double MAX_ANGLE = Math.toRadians(20);
    private final Location center;
    private Vector facing;
    private final Entity durabilityEntityReal;
    private final List<EntityLocation> turretEntities;
    private final EntityLocation durabilityEntity;
    private final EntityLocation refilledEntity;
    private final EntityLocation bowEntity;
    private final List<Pair<Material, Integer>> arrows;
    private final Material bow;
    private final int bowDurability;
    private double health;
    private long uid;
    private boolean isDead = false;
    private final long callerUid = UpdatedPlayerList.callerUid();
    private Player target = null;


    public TurretMob(UUID worldUid, double x, double y, double z,
                     double facingX, double facingY, double facingZ,
                     List<EntityLocation> turretEntities,
                     Entity durabilityEntityReal,
                     EntityLocation durabilityEntity, EntityLocation refilledEntity, EntityLocation bowEntity,
                     double health,
                     List<Pair<Material, Integer>> arrows,
                     Material bow,
                     int bowDurability
    ) {
        final World world = Bukkit.getWorld(worldUid);
        this.center = new Location(world, x, y, z);
        this.center.setDirection(new Vector(facingX, facingY, facingZ));
        this.facing = center.getDirection();
        this.turretEntities = turretEntities;
        this.durabilityEntityReal = durabilityEntityReal;
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
                     List<EntityLocation> turretEntities,
                     EntityLocation durabilityEntity, EntityLocation refilledEntity, EntityLocation bowEntity,
                     double health,
                     List<Pair<Material, Integer>> arrows,
                     Material bow,
                     int bowDurability,
                     long uid
    ) {
        final World world = Bukkit.getWorld(worldUid);
        this.center = new Location(world, x, y, z);
        this.center.setDirection(new Vector(facingX, facingY, facingZ));
        this.facing = center.getDirection();
        this.turretEntities = turretEntities;
        this.durabilityEntityReal = Bukkit.getEntity(durabilityEntity.uuid);
        if (this.durabilityEntityReal == null) isDead = true;
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
        if (health <= 0) {
            isDead = true;
        }
        new Thread(this).start();
    }

    public void tick() {
        if (target == null) {
            List<Player> players = UpdatedPlayerList.getPlayers(callerUid);
            for (Player player : players) {
                double distance = DistanceUtils.distance(player.getLocation(), center);
                if (distance <= MAX_SIGHT && player.hasLineOfSight(durabilityEntityReal)) {
                    final Vector newFacing = player.getLocation().subtract(center).getDirection().setY(0).normalize();
                    if (rotate(newFacing)) {
                        target = player;
                        return;
                    }
                }
            }
        } else {
            double distance = DistanceUtils.distance(target.getLocation(), center);
            if (distance <= MAX_SIGHT && target.hasLineOfSight(durabilityEntityReal)) {
                final Vector newFacing = target.getLocation().subtract(center).getDirection().setY(0).normalize();
                if (rotate(newFacing)) {
                    shoot(target);
                }
            } else {
                target = null;
            }
        }
    }

    private boolean rotate(Vector newFacing) {
        newFacing = newFacing.clone();
        Vector oldFacing = this.center.getDirection();
        newFacing.setY(oldFacing.getY());
        double xn = newFacing.getX();
        double zn = newFacing.getZ();
        double xo = oldFacing.getX();
        double zo = oldFacing.getZ();
        double angle = (xo * zo + xn * zn) / (Math.sqrt(xo * xo + zo * zo) * Math.sqrt(xn * xn + zn * zn));
        if (Math.abs(angle) < MAX_ANGLE) {
            // rotate by "angle" degrees
            for (EntityLocation entity : turretEntities) {
                rotate(entity, newFacing, center, angle);
            }
            rotate(bowEntity, newFacing, center, angle);
            rotate(durabilityEntity, newFacing, center, angle);
            rotate(refilledEntity, newFacing, center, angle);
            this.facing = newFacing;
            return true;
        }
        return false;
    }

    private static void rotate(EntityLocation entityLocation, Vector newFacing, Location center, double angle) {
        double radians = Math.toRadians(angle);
        double radius = DistanceUtils.magnitude(
                center.getX() - entityLocation.x,
                center.getY() - entityLocation.y,
                center.getZ() - entityLocation.z);
        @Nullable Entity entity = Bukkit.getEntity(entityLocation.uuid);
        if (entity != null) {
            double x = Math.cos(radians) * radius + center.getX();
            double z = Math.sin(radians) * radius + center.getZ();
            Location newLocation = entity.getLocation().setDirection(newFacing);
            newLocation.setX(x);
            newLocation.setZ(z);
            entity.teleport(newLocation);
        }
    }


    private void shoot(Player target) {

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


    @Override
    public int hashCode() {
        return (int) (this.uid % Integer.MAX_VALUE);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TurretMob && this.uid == ((TurretMob) obj).uid;
    }

    public Location getCenter() {
        return center;
    }

    public List<EntityLocation> getTurretEntities() {
        return turretEntities;
    }

    public EntityLocation getDurabilityEntity() {
        return durabilityEntity;
    }

    public EntityLocation getRefilledEntity() {
        return refilledEntity;
    }

    public EntityLocation getBowEntity() {
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

    public void setUniqueId(long uid) {
        this.uid = uid;
    }

    public boolean isDead() {
        return isDead;
    }
}
