package apple.voltskiya.custom_mobs.turrets;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.sql.DBItemStack;
import apple.voltskiya.custom_mobs.sql.DBUtils;
import apple.voltskiya.custom_mobs.sql.TurretsSql;
import apple.voltskiya.custom_mobs.turrets.gui.TurretGuiManager;
import apple.voltskiya.custom_mobs.turrets.gui.TurretTarget;
import apple.voltskiya.custom_mobs.util.DistanceUtils;
import apple.voltskiya.custom_mobs.util.EntityLocation;
import apple.voltskiya.custom_mobs.util.VectorUtils;
import apple.voltskiya.custom_mobs.util.minecraft.EnchantmentUtils;
import apple.voltskiya.custom_mobs.util.minecraft.EntityUtils;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftArrow;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
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
    private LivingEntity target = null;
    private final List<Vector> targetLastLocation = new ArrayList<>();
    private boolean isUpdatingDB = false;
    private final TurretType turretType;
    private TurretTarget.TurretTargetType targetType;
    private int tickIndex = 0;

    public TurretMob(UUID worldUid, double x, double y, double z,
                     double facingX, double facingY, double facingZ,
                     List<EntityLocation> turretEntities,
                     Entity durabilityEntityReal,
                     EntityLocation durabilityEntity, EntityLocation refilledEntity, EntityLocation bowEntity,
                     double health,
                     List<DBItemStack> arrows,
                     ItemStack bow,
                     long bowId,
                     TurretType turretType,
                     TurretTarget.TurretTargetType targetType
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
        this.targetType = targetType;
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
                     TurretType turretType,
                     TurretTarget.TurretTargetType targetType
    ) {
        final World world = Bukkit.getWorld(worldUid);
        this.center = new Location(world, x, y, z);
        this.center.setDirection(new Vector(facingX, facingY, facingZ));
        this.facing = center.getDirection().clone();
        this.turretEntities = turretEntities;
        this.durabilityEntityReal = Bukkit.getEntity(durabilityEntity.uuid);
        if (this.durabilityEntityReal == null) remove();
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
        this.targetType = targetType;
        correctRefilledEntity();
        correctBowEntity();
        correctDurabilityEntity();
    }

    public synchronized void damage(double damage) {
        this.health -= damage;
        if (health <= 0) {
            remove();
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
        this.isDead = true;
    }

    public void resetRotate() {
        rotate(center.getDirection());
    }

    public void tick() {
        if (isDead() || this.durabilityEntityReal.isDead() || Bukkit.getEntity(durabilityEntity.uuid) == null) remove();
        if (target == null) {
            // if we shouldn't target, don't target
            if (targetType == TurretTarget.TurretTargetType.NONE) return;
            for (Entity entity : this.durabilityEntityReal.getNearbyEntities(MAX_SIGHT, MAX_SIGHT, MAX_SIGHT)) {
                if (shouldTarget(entity)) {
                    target = (LivingEntity) entity;
                    Location goal = target.getLocation();
                    this.targetLastLocation.add(goal.toVector());
                    if (this.tickIndex % ticksPerShoot() == 0) {
                        this.tickIndex = 0;
                        shoot(target);
                    }
                    break;
                }
            }
        } else {
            if (shouldTarget(target)) {
                Location goal = target.getLocation();
                this.targetLastLocation.add(goal.toVector());
                if (this.tickIndex % ticksPerShoot() == 0) {
                    this.tickIndex = 0;
                    shoot(target);
                }
            } else {
                this.targetLastLocation.clear();
                resetRotate();
                target = null;
            }
        }
        this.tickIndex++;
    }

    /**
     * @return how many skipped ticks before shooting
     */
    private int ticksPerShoot() {
        if (this.bow == null) return 1;
        final int quickCharge = this.bow.getEnchantmentLevel(Enchantment.QUICK_CHARGE);
        switch (quickCharge) {
            case 0:
                return 4;
            case 1:
                return 3;
            case 2:
                return 2;
            default:
                return 1;
        }
    }

    private boolean shouldTarget(@Nullable Entity entity) {
        if (entity instanceof LivingEntity) {
            LivingEntity alive = (LivingEntity) entity;
            double distance = DistanceUtils.distance(entity.getLocation(), center);
            if (!alive.isDead() && alive.hasLineOfSight(durabilityEntityReal) && distance < MAX_SIGHT) {
                final Vector newFacing = entity.getLocation().subtract(center).toVector().setY(0).normalize();
                if (entity instanceof Player) {
                    return this.targetType.isTargetsPlayers() && ((Player) entity).getGameMode() == GameMode.SURVIVAL && rotate(newFacing);
                } else {
                    return EntityUtils.isHostile(alive)&& this.targetType.isTargetsMobs() && rotate(newFacing);
                }
            }
        }
        return false;
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


    private void shoot(LivingEntity target) {
        if (arrowsEmpty() || noBow()) return;
        Location goal = target.getEyeLocation();
        int lastLocationSize = this.targetLastLocation.size();
        while (lastLocationSize > MAX_TARGET_RECORDING) {
            this.targetLastLocation.remove(0);
            lastLocationSize--;
        }
        double distanceToTarget = DistanceUtils.distance(goal, center);

        if (distanceToTarget < MAX_SIGHT && distanceToTarget > MIN_SIGHT) {
            Location spawnLocation = center.clone();
            spawnLocation.add(facing);
            spawnLocation.add(facing);
            spawnLocation.add(facing);
            spawnLocation.add(0, 2.5, 0);
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
                    entity.setVelocity(new Vector(vx * .25, vy * .25, vz * .25));
                    if (entity instanceof Egg || entity instanceof Snowball) {
                        return;
                    }
                    CraftArrow arrow = (CraftArrow) entity;
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
                    arrow.addScoreboardTag("no_invincibility_mobs");
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
            if (arrow.type != Material.AIR && count > 0) {
                if (turretType != TurretType.INFINITE) {
                    if (count <= 1) {
                        arrow.type = Material.AIR;
                    } else {
                        arrow.count--;
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
            if (VoltskiyaPlugin.get().isEnabled())
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

    public TurretTarget.TurretTargetType getTargetType() {
        return this.targetType;
    }

    public void setTargetType(TurretTarget.TurretTargetType targetType) {
        this.targetType = targetType;
        if (isOkayToStart()) {
            new Thread(this).start();
        }
    }
}
