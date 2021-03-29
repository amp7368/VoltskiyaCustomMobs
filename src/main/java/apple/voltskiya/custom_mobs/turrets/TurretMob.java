package apple.voltskiya.custom_mobs.turrets;

import apple.voltskiya.custom_mobs.sql.TurretsSql;
import apple.voltskiya.custom_mobs.util.DistanceUtils;
import apple.voltskiya.custom_mobs.util.Pair;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;
import apple.voltskiya.custom_mobs.util.VectorUtils;
import org.bukkit.*;
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
    protected static final double MAX_ANGLE = Math.toRadians(90);
    private static final double VELOCITY = 7.0; // velocity of the arrow
    private static final double GRAVITY = -1.0; // gravity
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
        this.facing = center.getDirection().clone();
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
        this.facing = center.getDirection().clone();
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
                    final Vector newFacing = player.getLocation().subtract(center).toVector().setY(0).normalize();
                    if (rotate(newFacing)) {
                        target = player;
                        return;
                    } else {
                        rotate(center.getDirection());
                    }
                }
            }
        } else {
            double distance = DistanceUtils.distance(target.getLocation(), center);
            if (distance <= MAX_SIGHT && target.hasLineOfSight(durabilityEntityReal) && target.getGameMode() == GameMode.SURVIVAL) {
                final Vector newFacing = target.getLocation().subtract(center).toVector().setY(0).normalize();
                if (rotate(newFacing)) {
                    shoot(target);
                } else {
                    rotate(center.getDirection());
                }
            } else {
                target = null;
            }
        }
    }

    /**
     * rotates the turret to face the newFacing vector
     *
     * @param newFacing the new vector to face
     * @return whether the turret can rotate to that degree
     */
    private boolean rotate(Vector newFacing) {
        newFacing = newFacing.clone();
        Vector oldFacing = this.center.getDirection().clone();
        newFacing.setY(oldFacing.getY());
        oldFacing.setY(0).normalize();
        double xn = newFacing.getX();
        double zn = newFacing.getZ();
        double xo = oldFacing.getX();
        double zo = oldFacing.getZ();

        double angleN = Math.atan2(zn, xn);
        double angleO = Math.atan2(zo, xo);
        double angle = Math.abs(angleN - angleO);
        angle %= Math.PI * 2;
        while (angle < 0) angle += Math.PI * 2;
        System.out.println(Math.toDegrees(angle));
        if (angle < MAX_ANGLE || Math.abs(Math.PI * 2 - angle) < MAX_ANGLE) {
            // rotate by "angleN" degrees
            for (EntityLocation entity : turretEntities) {
                rotate(entity, newFacing, center);
            }
            this.facing = newFacing;
            return true;
        }
        return false;
    }


    private static void rotate(EntityLocation entityLocation, Vector newFacing, Location center) {
        @Nullable Entity entity = Bukkit.getEntity(entityLocation.uuid);
        if (entity != null) {
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
            double theta = Math.atan2(newFacing.getZ(), newFacing.getX()) - Math.toRadians(90); // todo make this 0
            while (theta < 0) theta += Math.PI * 2;
            Vector newEntityFacing = VectorUtils.rotateVector(entityLocation.x, entityLocation.z, entityLocation.xFacing, entityLocation.zFacing, entityLocation.yFacing, theta);
            Location newLocation = entity.getLocation().setDirection(newEntityFacing);
            newLocation.setX(x);
            newLocation.setZ(z);
            entity.teleport(newLocation);
        }
    }


    private void shoot(Player target) {
        double distance = DistanceUtils.distance(target.getLocation(), center);
        if (distance < MAX_SIGHT) {
            Location spawnLocation = center.clone();
            Vector forward = facing.clone().setY(0);
            spawnLocation.add(forward).add(forward).add(forward);
            spawnLocation.add(0, 1.5, 0);
            // c = g*x/(2*v*v)
            //
            //            -1 +/- sqrt( 1 - 4(c)(c-(y/x)) )
            // tan(O) =   --------------------------------
            //                         2(c)
            double a = target.getLocation().getX() - spawnLocation.getX();
            double b = target.getLocation().getZ() - spawnLocation.getZ();
            double x = Math.sqrt(a * a + b * b);
            double y = target.getLocation().getY() - spawnLocation.getY();
            double v = VELOCITY;
            double c = GRAVITY * x / (2 * v * v);
            // this could be a minus as well
            double theta = Math.atan((-1 - Math.sqrt(1 + 4 * c * (c - y / x))) / (2 * c));
            double vxz = v * Math.cos(theta);
            double vy = v * Math.sin(theta);

            double xzTheta = Math.atan2(b, a);
            double vx = vxz * Math.cos(xzTheta);
            double vz = vxz * Math.sin(xzTheta);
            spawnLocation.getWorld().spawnArrow(spawnLocation, new Vector(vx, vy, vz), (float) (v * 0.25), 0);
        }
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
