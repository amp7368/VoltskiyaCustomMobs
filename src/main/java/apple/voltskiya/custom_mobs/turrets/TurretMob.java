package apple.voltskiya.custom_mobs.turrets;

import apple.voltskiya.custom_mobs.sql.DBItemStack;
import apple.voltskiya.custom_mobs.sql.DBUtils;
import apple.voltskiya.custom_mobs.sql.TurretsSql;
import apple.voltskiya.custom_mobs.turrets.gui.TurretGuiManager;
import apple.voltskiya.custom_mobs.util.DistanceUtils;
import apple.voltskiya.custom_mobs.util.EntityLocation;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;
import apple.voltskiya.custom_mobs.util.VectorUtils;
import apple.voltskiya.custom_mobs.util.minecraft.EnchantmentUtils;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftArrow;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
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
    private static final double BASE_TURRET_DAMAGE = 2;
    private final Location center;
    private Vector facing;
    private final Entity durabilityEntityReal;
    private final Entity refilledEntityReal;
    private final Entity bowEntityReal;
    private final List<EntityLocation> turretEntities;
    private final EntityLocation durabilityEntity;
    private final EntityLocation refilledEntity;
    private final EntityLocation bowEntity;
    private List<DBItemStack> arrows;
    private ItemStack bow;
    private long bowId;
    private double health;
    private long uid;
    private boolean isDead = false;
    private final long callerUid = UpdatedPlayerList.callerUid();
    private Player target = null;
    private final List<Vector> targetLastLocation = new ArrayList<>();
    private boolean isUpdatingDB = false;
    private final TurretType turretType;

    public TurretMob(UUID worldUid, double x, double y, double z,
                     double facingX, double facingY, double facingZ,
                     List<EntityLocation> turretEntities,
                     Entity durabilityEntityReal,
                     EntityLocation durabilityEntity, EntityLocation refilledEntity, EntityLocation bowEntity,
                     double health,
                     List<DBItemStack> arrows,
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
        this.refilledEntityReal = Bukkit.getEntity(refilledEntity.uuid);
        this.bowEntity = bowEntity;
        this.bowEntityReal = Bukkit.getEntity(bowEntity.uuid);
        this.health = health;
        this.arrows = arrows;
        this.bow = bow;
        this.bowId = bowId;
        this.uid = -1;
        this.turretType = turretType;
    }

    public TurretMob(UUID worldUid, double x, double y, double z,
                     double facingX, double facingY, double facingZ,
                     List<EntityLocation> turretEntities,
                     EntityLocation durabilityEntity, EntityLocation refilledEntity, EntityLocation bowEntity,
                     double health,
                     List<DBItemStack> arrows,
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
        this.refilledEntityReal = Bukkit.getEntity(refilledEntity.uuid);
        this.bowEntity = bowEntity;
        this.bowEntityReal = Bukkit.getEntity(bowEntity.uuid);
        this.health = health;
        this.arrows = arrows;
        this.bow = bow;
        this.bowId = bowId;
        this.uid = uid;
        this.turretType = turretType;
    }

    public synchronized void damage(double damage) {
        this.health -= damage;
        if (health <= 0) {
            isDead = true;
            remove();
            try {
                TurretsSql.removeTurret(uid);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (this.isOkayToStart()) {
            new Thread(this).start();
        }
        TurretGuiManager.get().updateGui(getUniqueId());
        correctDurabilityEntity();
    }

    private void correctDurabilityEntity() {
        if (durabilityEntityReal instanceof ArmorStand) {
            final EntityEquipment equipment = ((ArmorStand) durabilityEntityReal).getEquipment();
            if (equipment != null) {
                if (health > MAX_HEALTH * .75) {
                    equipment.setHelmet(new ItemStack(Material.CHISELED_STONE_BRICKS));
                } else if (health > MAX_HEALTH * .5) {
                    equipment.setHelmet(new ItemStack(Material.STONE_BRICKS));
                } else if (health > MAX_HEALTH * .25) {
                    equipment.setHelmet(new ItemStack(Material.CRACKED_STONE_BRICKS));
                } else {
                    equipment.setHelmet(new ItemStack(Material.COBBLESTONE));
                }
            }
        }
    }

    private void correctRefilledEntity() {
        if (this.refilledEntityReal instanceof ArmorStand) {
            final EntityEquipment equipment = ((ArmorStand) refilledEntityReal).getEquipment();
            if (equipment != null) {
                if (noBow() || arrowsEmpty()) {
                    final ItemStack torch = new ItemStack(Material.SOUL_TORCH);
                    equipment.setHelmet(torch);
                } else {
                    equipment.setHelmet(new ItemStack(Material.REDSTONE_TORCH));
                }
            }
        }
    }

    private void correctBowEntity() {
        if (this.bowEntityReal instanceof ArmorStand) {
            final EntityEquipment equipment = ((ArmorStand) bowEntityReal).getEquipment();
            if (equipment != null) {
                if (noBow()) {
                    final ItemStack torch = new ItemStack(Material.AIR);
                    equipment.setHelmet(torch);
                } else {
                    equipment.setHelmet(new ItemStack(bow.getType()));
                }
            }
        }
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
        if (this.durabilityEntityReal.isDead()) remove();
        if (target == null) {
            List<Player> players = UpdatedPlayerList.getPlayers(callerUid);
            for (Player player : players) {
                double distance = DistanceUtils.distance(player.getLocation(), center);
                if (distance <= MAX_SIGHT && player.hasLineOfSight(durabilityEntityReal) && player.getGameMode() == GameMode.SURVIVAL) {
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
            if (distance <= MAX_SIGHT && target.hasLineOfSight(durabilityEntityReal) && target.getGameMode() == GameMode.SURVIVAL && !target.isDead()) {
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
                Location myFacing = new Location(null, 0, 0, 0);
                myFacing.setDirection(new Vector(entity.xFacing, entity.yFacing, entity.zFacing));
                float yaw1 = myFacing.getYaw();
                myFacing.setDirection(newFacing);
                float yaw2 = myFacing.getYaw();
                myFacing.setYaw(yaw1 + yaw2);

                VectorUtils.rotate(entity, newFacing, center, true);
            }
            this.facing = newFacing;
            return true;
        }
        return false;
    }


    private void shoot(Player target) {
        if (arrowsEmpty() || noBow()) return;
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
            // this could be a plus as well
            double theta = Math.atan((-1 + Math.sqrt(1 + 4 * c * (c - y / x))) / (2 * c));
            double vxz = v * Math.cos(theta);
            double vy = v * -Math.sin(theta);

            double xzTheta = Math.atan2(b, a);
            double vx = vxz * Math.cos(xzTheta);
            double vz = vxz * Math.sin(xzTheta);
            @Nullable DBItemStack removedArrow = removeArrow();
            if (removedArrow == null || !removedArrow.exists()) {
                return;
            }
            this.tickBowDurability();

            @Nullable EntityType arrowEntity = removedArrow.toEntityType();
            if (arrowEntity != null) {
                spawnLocation.getWorld().spawnEntity(spawnLocation, arrowEntity, CreatureSpawnEvent.SpawnReason.CUSTOM, (entity) -> {
                    CraftArrow arrow = (CraftArrow) entity;
                    arrow.setVelocity(new Vector(vx * .25, vy * .25, vz * .25));
                    if (removedArrow.hasNbt()) {
                        final NBTTagCompound nbt = removedArrow.getEntityNbt();
                        if (nbt != null)
                            arrow.getHandle().loadData(nbt);
                    }
                    if (this.bow.getType() == Material.CROSSBOW) {
                        arrow.setShotFromCrossbow(true);
                    }
                    arrow.setDamage(EnchantmentUtils.damage(BASE_TURRET_DAMAGE, this.bow.getEnchantmentLevel(Enchantment.ARROW_DAMAGE)));
                    arrow.setKnockbackStrength(EnchantmentUtils.knockback(this.bow.getEnchantmentLevel(Enchantment.ARROW_KNOCKBACK)));
                    arrow.setFireTicks(EnchantmentUtils.flame(this.bow.getEnchantmentLevel(Enchantment.ARROW_FIRE)));
                    arrow.setPierceLevel(this.bow.getEnchantmentLevel(Enchantment.PIERCING));
                    arrow.addScoreboardTag("no_stick");
                });
            }
            if (this.isOkayToStart()) {
                new Thread(this).start();
            }
            TurretGuiManager.get().updateGui(getUniqueId());
        }
    }

    private void tickBowDurability() {
        if (turretType != TurretType.INFINITE) {
            if (this.bow != null) {
                ItemMeta itemMeta = this.bow.getItemMeta();
                if (itemMeta instanceof Damageable) {
                    int unbreaking = this.bow.getEnchantmentLevel(Enchantment.DURABILITY);
                    boolean doBreak = EnchantmentUtils.randomBreakUnbreaking(unbreaking);
                    if (doBreak) {
                        ((Damageable) itemMeta).setDamage(((Damageable) itemMeta).getDamage() + 1);
                        this.bow.setItemMeta(itemMeta);
                        if (((Damageable) itemMeta).getDamage() >= this.bow.getType().getMaxDurability()) {
                            // delete bow
                            this.bow = new ItemStack(Material.AIR);
                            this.correctBowEntity();
                        }
                    }
                }
                correctRefilledEntity();
            }
        }
    }

    private boolean noBow() {
        if (this.bow == null || this.bow.getType().isAir()) return true;
        ItemMeta meta = this.bow.getItemMeta();
        return meta instanceof Damageable && ((Damageable) meta).getDamage() >= this.bow.getType().getMaxDurability();
    }

    private DBItemStack removeArrow() {
        for (DBItemStack arrow : arrows) {
            final int count = arrow.count;
            if (arrow.type != Material.AIR && count != 0) {
                if (turretType != TurretType.INFINITE) {
                    if (count == 1) {
                        arrow.type = Material.AIR;
                    }
                    if (isOkayToStart()) {
                        new Thread(this).start();
                    }
                    TurretGuiManager.get().updateGui(getUniqueId());
                }
                return arrow;
            }
        }
        return null;
    }

    public void setArrows(List<DBItemStack> arrows) {
        this.arrows = arrows;
        this.correctRefilledEntity();
        if (isOkayToStart()) {
            new Thread(this).start();
        }
        TurretGuiManager.get().updateGui(getUniqueId());
    }


    public void repair(int repairAmount) {
        this.health = Math.min(MAX_HEALTH, repairAmount * HEALTH_PER_REPAIR + health);
        if (isOkayToStart()) {
            new Thread(this).start();
        }
        TurretGuiManager.get().updateGui(getUniqueId());
    }

    public void rotateCenter(double degrees) {
        final Vector direction = center.getDirection();
        center.setDirection(VectorUtils.rotateVector(direction.getX(), direction.getZ(), direction.getY(), degrees));
        resetRotate();
        if (isOkayToStart()) {
            new Thread(this).start();
        }
        TurretGuiManager.get().updateGui(getUniqueId());
    }

    private boolean arrowsEmpty() {
        for (DBItemStack arrow : arrows) {
            if (arrow.exists()) return false;
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
        } catch (InterruptedException ignored) {
        }
        // do this before to make sure we don't say finish before we say we're done
        synchronized (this) {
            this.isUpdatingDB = false;
        }
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

    /**
     * @return the old value of isUpdatingDB
     */
    public boolean isOkayToStart() {
        synchronized (this) {
            boolean old = this.isUpdatingDB;
            this.isUpdatingDB = true;
            return !old;
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

    public List<DBItemStack> getArrows() {
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

    public void setBow(ItemStack bow) {
        try {
            DBUtils.removeItemUid(this.bowId);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            this.bow = bow;
            this.bowId = DBUtils.getItemUid(this.bow);
            this.correctBowEntity();
            this.correctRefilledEntity();
            if (isOkayToStart()) {
                new Thread(this).start();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
