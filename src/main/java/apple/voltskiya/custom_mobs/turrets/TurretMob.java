package apple.voltskiya.custom_mobs.turrets;

import apple.voltskiya.custom_mobs.sql.TurretsSql;
import apple.voltskiya.custom_mobs.sql.VerifyMobsSql;
import apple.voltskiya.custom_mobs.turrets.gui.TurretGuiManager;
import apple.voltskiya.custom_mobs.util.DistanceUtils;
import apple.voltskiya.custom_mobs.util.Pair;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;
import apple.voltskiya.custom_mobs.util.VectorUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TurretMob implements Runnable {
    public static final String TURRET_TAG = "player.turret";
    private static final int MAX_SIGHT = 50;
    protected static final int MAX_HEALTH = 200;
    public static final double HEALTH_PER_REPAIR = 10;
    protected static final double MAX_ANGLE = Math.toRadians(90);
    private static final double VELOCITY = 7.0; // velocity of the arrow
    private static final double GRAVITY = -1.0; // gravity
    private static final long BUFFER_TIME_TO_UPDATE = 10000;
    private static final int MAX_TARGET_RECORDING = 5;
    private static final double MIN_SIGHT = 3.5;
    private final Location center;
    private Vector facing;
    private final Entity durabilityEntityReal;
    private final List<EntityLocation> turretEntities;
    private final EntityLocation durabilityEntity;
    private final EntityLocation refilledEntity;
    private final EntityLocation bowEntity;
    private List<Pair<Material, Integer>> arrows;
    private final ItemStack bow;
    private final long bowId;
    private double health;
    private long uid;
    private boolean isDead = false;
    private final long callerUid = UpdatedPlayerList.callerUid();
    private Player target = null;
    private final List<Vector> targetLastLocation = new ArrayList<Vector>();
    private boolean isUpdatingDB = false;
    private final TurretType turretType;

    public TurretMob(UUID worldUid, double x, double y, double z,
                     double facingX, double facingY, double facingZ,
                     List<EntityLocation> turretEntities,
                     Entity durabilityEntityReal,
                     EntityLocation durabilityEntity, EntityLocation refilledEntity, EntityLocation bowEntity,
                     double health,
                     List<Pair<Material, Integer>> arrows,
                     ItemStack bow,
                     long bowId,
                     TurretType turretType
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
        this.bowId=bowId;
        this.uid = -1;
        this.turretType = turretType;
    }

    public TurretMob(UUID worldUid, double x, double y, double z,
                     double facingX, double facingY, double facingZ,
                     List<EntityLocation> turretEntities,
                     EntityLocation durabilityEntity, EntityLocation refilledEntity, EntityLocation bowEntity,
                     double health,
                     List<Pair<Material, Integer>> arrows,
                     ItemStack bow,
                     long uid,
                     long bowId,
                     TurretType turretType
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
        this.bowId=bowId;
        this.uid = uid;
        this.turretType = turretType;
    }

    public synchronized void damage(double damage) {
        this.health -= damage;
        System.out.println(this.health);
        if (health <= 0) {
            isDead = true;
            remove();
            try {
                TurretsSql.removeTurret(uid);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (!this.isOkayToStart()) {
            new Thread(this).start();
        }
        TurretGuiManager.get().updateGui(getUniqueId());
    }

    private void remove() {
        TurretManagerTicker.get().removeTurret(this.uid, this.turretEntities);
        try {
            TurretsSql.removeTurret(uid);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void resetRotate() {
        rotate(center.getDirection());
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
                    target = null;
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
        if (arrowsEmpty()) return;
        Location goal = target.getLocation();
        this.targetLastLocation.add(goal.toVector());
        int lastLocationSize = this.targetLastLocation.size();
        if (lastLocationSize > MAX_TARGET_RECORDING) {
            this.targetLastLocation.remove(0);
            lastLocationSize--;
        }
        double distanceToTarget = DistanceUtils.distance(goal, center);

        if (distanceToTarget < MAX_SIGHT && distanceToTarget > MIN_SIGHT) {
            Location spawnLocation = center.clone();
            spawnLocation.add(facing);
            spawnLocation.add(facing);
            spawnLocation.add(facing);
            spawnLocation.add(0, 1.5, 0);
            double v = velocity(distanceToTarget);
            double timeToTarget = distanceToTarget / v * .9;
            Vector movement = lastLocationSize <= 1 ?
                    new Vector(0, 0, 0) :
                    this.targetLastLocation.get(lastLocationSize - 1).clone().subtract(
                            this.targetLastLocation.get(0)
                    ).divide(
                            new Vector(lastLocationSize - 1, lastLocationSize - 1, lastLocationSize - 1)
                    ).multiply(timeToTarget);
            goal.add(movement);

            // c = g*x/(2*v*v)
            //
            //            -1 +/- sqrt( 1 - 4(c)(c-(y/x)) )
            // tan(O) =   --------------------------------
            //                         2(c)
            double a = goal.getX() - spawnLocation.getX();
            double b = goal.getZ() - spawnLocation.getZ();
            double x = Math.sqrt(a * a + b * b);
            double y = goal.getY() - spawnLocation.getY();
            double c = GRAVITY * x / (2 * v * v);
            // this could be a minus as well
            double theta = Math.atan((-1 + Math.sqrt(1 + 4 * c * (c - y / x))) / (2 * c));
            double vxz = v * Math.cos(theta);
            double vy = v * -Math.sin(theta);

            double xzTheta = Math.atan2(b, a);
            double vx = vxz * Math.cos(xzTheta);
            double vz = vxz * Math.sin(xzTheta);
            @Nullable Material removedArrow = removeArrow();
            if (removedArrow == null || removedArrow.isAir()) {
                return;
            }
            EntityType arrowEntity;
            try {
                arrowEntity = EntityType.valueOf(removedArrow.name());
            } catch (IllegalArgumentException e) {
                // this arrow doesn't exist
                e.printStackTrace();
                return;
            }
            spawnLocation.getWorld().spawnEntity(spawnLocation, arrowEntity, CreatureSpawnEvent.SpawnReason.CUSTOM, (arrow) -> {
                arrow.setVelocity(new Vector(vx * .25, vy * .25, vz * .25));
                arrow.addScoreboardTag("no_stick");
            });
            if (!this.isOkayToStart()) {
                new Thread(this).start();
            }
            TurretGuiManager.get().updateGui(getUniqueId());
        }
    }

    @Nullable
    private Material removeArrow() {
        for (Pair<Material, Integer> arrow : arrows) {
            final int count = arrow.getValue();
            if (arrow.getKey() != Material.AIR && count != 0) {
                if (turretType != TurretType.INFINITE) {
                    arrow.setValue(count - 1);
                    if (count == 1) {
                        arrow.setKey(Material.AIR);
                    }
                }
                if (!isOkayToStart()) {
                    new Thread(this).start();
                }
                TurretGuiManager.get().updateGui(getUniqueId());
                return arrow.getKey();
            }
        }
        return null;
    }

    public void setArrows(List<Pair<Material, Integer>> arrows) {
        this.arrows = arrows;
        if (!isOkayToStart()) {
            new Thread(this).start();
        }
        TurretGuiManager.get().updateGui(getUniqueId());
    }


    public void repair(int repairAmount) {
        this.health = Math.min(MAX_HEALTH, repairAmount * HEALTH_PER_REPAIR + health);
        if (!isOkayToStart()) {
            new Thread(this).start();
        }
        TurretGuiManager.get().updateGui(getUniqueId());
    }

    public void rotateCenter(double degrees) {
        final Vector direction = center.getDirection();
        center.setDirection(VectorUtils.rotateVector(direction.getX(), direction.getZ(), direction.getY(), degrees));
        resetRotate();
        if (!isOkayToStart()) {
            new Thread(this).start();
        }
        TurretGuiManager.get().updateGui(getUniqueId());
    }

    private boolean arrowsEmpty() {
        for (Pair<Material, Integer> arrow : arrows) {
            if (arrow.getKey() != Material.AIR && arrow.getValue() != 0) return false;
        }
        return true;
    }

    private double velocity(double distanceToTarget) {
        //  20     distance
        // ---- = ----------
        //   V         ?
        // v*distance/20;
        return Math.max(VELOCITY, VELOCITY * distanceToTarget / 15);
    }

    /**
     * update the database
     */
    @Override
    public void run() {
        try {
            Thread.sleep(BUFFER_TIME_TO_UPDATE);
        } catch (InterruptedException e) {
        }

        synchronized (VerifyMobsSql.syncDB) {
            synchronized (this) {
                this.isUpdatingDB = false;
            }
        }
        try {
            TurretsSql.registerOrUpdate(this);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println("updated");
    }


    @Override
    public int hashCode() {
        return (int) (this.uid % Integer.MAX_VALUE);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TurretMob && this.uid == ((TurretMob) obj).uid;
    }

    /**
     * @return the old value of isUpdatingDB
     */
    public boolean isOkayToStart() {
        synchronized (this) {
            boolean old = this.isUpdatingDB;
            this.isUpdatingDB = true;
            return old;
        }
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

    public ItemStack getBow() {
        return bow;
    }

    public long getBowId() {
        return bowId;
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

    public int getRepairCost() {
        return (int) Math.ceil((MAX_HEALTH - health) / HEALTH_PER_REPAIR);
    }

    public TurretType getTurretType() {
        return turretType;
    }
}
