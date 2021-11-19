package apple.voltskiya.custom_mobs.turret.parent;

import apple.utilities.database.SaveFileableKeyed;
import apple.utilities.util.NumberUtils;
import apple.voltskiya.custom_mobs.custom_model.spawning.CustomModelEntityImpl;
import apple.voltskiya.custom_mobs.turret.manage.TurretDatabase;
import apple.voltskiya.custom_mobs.turret.manage.TurretSpawnListener;
import apple.voltskiya.custom_mobs.turret.manage.TurretTypeIdentifier;
import apple.voltskiya.custom_mobs.turret.manage.model.impl.TurretModelImpl;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftArrow;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.utilities.util.DistanceUtils;
import voltskiya.apple.utilities.util.VectorUtils;
import voltskiya.apple.utilities.util.event_listener.manage.EntityEventListener;
import voltskiya.apple.utilities.util.event_listener.manage.ListenerManaged;
import voltskiya.apple.utilities.util.gui.acd.InventoryGuiACD;
import voltskiya.apple.utilities.util.minecraft.EnchantmentUtils;
import voltskiya.apple.utilities.util.minecraft.EntityUtils;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class TurretMob<Config extends TurretMobConfig>
        implements SaveFileableKeyed,
        EntityEventListener.EntityDamageByEntity,
        EntityEventListener.PlayerInteractAtEntity {
    protected static final double GRAVITY = -4.8;

    protected transient Config config = null;
    protected transient int tickIndex = 0;
    protected transient boolean isDirty = false;
    protected TurretModelImpl turretModel;
    protected UUID uuid;
    protected boolean isDead;
    protected double health;
    protected double rotation;
    protected TurretBow bow;
    private transient InventoryGuiACD gui = null;


    public TurretMob() {
    }

    public TurretMob(TurretModelImpl turretModel) {
        this.turretModel = turretModel;
        Entity main = turretModel.getMain();
        this.uuid = main == null ? UUID.randomUUID() : main.getUniqueId();
        this.isDead = false;
        this.health = getConfig().getMaxHealth();
        this.rotation = 0;
        this.bow = makeTurretBow();
    }

    public void initialize() {
        List<UUID> entities = this.turretModel.getEntities()
                .stream()
                .map(CustomModelEntityImpl::getUniqueId)
                .collect(Collectors.toList());
        ListenerManaged listener = new ListenerManaged(entities);
        registerListenerEntityDamageByEntity(listener);
        registerListenerPlayerInteractAtEntity(listener);
        setDirty();
        TurretSpawnListener.addToTicker(this);
    }

    @Override
    public void onEventEntityDamageByEntity(EntityDamageByEntityEvent event) {
        this.damage(event.getFinalDamage());
        event.setCancelled(true);
    }

    @Override
    public boolean shouldRemoveEntityDamageByEntity() {
        return isDead();
    }

    @Override
    public void onEventPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        this.openGui(event.getPlayer());
        event.setCancelled(true);
    }

    @Override
    public boolean shouldRemovePlayerInteractAtEntity() {
        return isDead();
    }

    protected void openGui(Player player) {
        if (this.gui == null) {
            this.gui = makeGui();
        }
        player.openInventory(this.gui.getInventory());
    }

    protected abstract InventoryGuiACD makeGui();

    protected abstract TurretTargeting getTargeting();

    protected TurretBow makeTurretBow() {
        return new TurretBow(config.getArrowStackSize(), config.getArrowSlots());
    }

    @Override
    public UUID getSaveId() {
        return uuid;
    }

    @Override
    public String getSaveFileName() {
        return extensionJson(uuid.toString());
    }

    public Location getLocation() {
        return turretModel.location;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public boolean shouldRemove() {
        return isDead();
    }

    public void killIfNotDead() {
        save();
        if (!this.isDead()) {
            this.isDead = true;
            turretModel.kill();
            kill();
        }
    }

    private void save() {
        if (isDead()) TurretDatabase.delete(this);
        else TurretDatabase.save(this);
    }

    protected abstract void kill();

    public void tick_(int tickSpeed) {
        tick(tickSpeed);
        if (isDirty) {
            isDirty = false;
            save();
        }
        this.tickIndex++;
    }

    public Config getConfig() {
        if (this.config == null)
            this.config = verifyConfig();
        return this.config;
    }

    protected abstract Config verifyConfig();


    public synchronized void damage(double damage) {
        this.health -= damage;
        if (health <= 0) {
            killIfNotDead();
        }
        setDirty();
        correctDurabilityEntity();
    }

    private void updateGui() {
        if (this.gui != null) {
            if (this.gui.getInventory().getViewers().isEmpty()) {
                this.gui = null;
            } else {
                this.gui.parentRefresh();
            }
        }
    }

    private void correctDurabilityEntity() {
        double maxHealth = getConfig().getMaxHealth();
        Material material;
        if (health > maxHealth * .85) {
            material = Material.SMOOTH_STONE;
        } else if (health > maxHealth * .75) {
            material = Material.CHISELED_STONE_BRICKS;
        } else if (health > maxHealth * .5) {
            material = Material.STONE_BRICKS;
        } else if (health > maxHealth * .25) {
            material = Material.CRACKED_STONE_BRICKS;
        } else {
            material = Material.COBBLESTONE;
        }
        turretModel.setDurabilityEntityHead(material);
    }

    private void correctRefilledEntity() {
        final Material torch;
        if (noBow() || bow.isArrowsEmpty()) {
            torch = Material.SOUL_TORCH;
        } else {
            torch = Material.REDSTONE_TORCH;
        }
        turretModel.setRefilledEntityHead(torch);
    }

    private void correctBowEntity() {
        this.turretModel.setBowEntity(bow.getHeadMaterial());
    }

    public void resetRotate() {
        rotation = turretModel.location.getYaw();
        rotate();
    }

    public void tick(int tickSpeed) {
        TurretTargeting targeting = getTargeting();
        if (targeting.getTarget() == null) {
            double maxSight = getConfig().getMaxSight();
            Collection<Entity> nearbyEntities = turretModel.location.getNearbyEntities(maxSight, maxSight, maxSight);
            for (Entity entity : EntityUtils.sortByClosest(nearbyEntities, turretModel.location)) {
                if (entity instanceof LivingEntity living && shouldTarget(living)) {
                    targeting.setTarget(living);
                    break;
                }
            }
        }
        if (targeting.getTarget() != null && shouldTarget(targeting.getTarget())) {
            getTargeting().tick();
            if (this.tickIndex % bow.ticksPerShoot() == 0) {
                shoot();
            }
        } else {
            resetRotate();
            targeting.clearTarget();
        }
    }

    public boolean shouldTarget(@NotNull LivingEntity entity) {
        double distance = DistanceUtils.distance(entity.getLocation(), turretModel.location);
        if (entity.isDead() ||
                !entity.hasLineOfSight(turretModel.location) ||
                !NumberUtils.betweenDouble(getConfig().getMinSight(), distance, getConfig().getMaxSight())) {
            return false;
        }
        final Vector newFacing = entity.getLocation().subtract(turretModel.location).toVector().setY(0).normalize();
        TurretTargeting targeting = getTargeting();
        rotation = VectorUtils.yaw(newFacing);
        boolean shouldTarget;
        if (entity instanceof Player player) {
            shouldTarget = targeting.shouldTargetPlayer(player);
        } else if (EntityUtils.isHostile(entity)) {
            shouldTarget = targeting.isTargetHostile(entity);
        } else {
            shouldTarget = targeting.isTargetEntity(entity);
        }
        return shouldTarget && rotate();
    }


    /**
     * rotates the turret to face the newFacing vector
     *
     * @return whether the turret can rotate to that degree
     */
    private boolean rotate() {
        double angleN = this.rotation;
        double angleO = turretModel.location.getYaw();
        double angle = Math.abs(angleN - angleO);
        double maxAngle = getConfig().maxRotationAngle();
        if (angle > maxAngle) return false;
        // rotate by "angleN" degrees
        turretModel.rotate(angleN);
        return true;
    }


    private void shoot() {
        if (bow.isArrowsEmpty() || noBow()) return;
        Location spawnLocation = turretModel.location.clone();
        spawnLocation.add(spawnLocation.getDirection().setY(0).normalize().multiply(3));
        spawnLocation.add(0, 2.5, 0);
        Location goal = getTargeting().getGoalLocation(spawnLocation, this::velocity);
        if (goal == null) return;
        double distanceToTarget = DistanceUtils.distance(goal, spawnLocation);
        if (!NumberUtils.betweenDouble(getConfig().getMinSight(), distanceToTarget, getConfig().getMaxSight())) {
            return;
        }
        double v = velocity(distanceToTarget);
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
        double vy = v * Math.sin(theta); //todo maybe negative here

        double xzTheta = Math.atan2(b, a);
        double vx = vxz * Math.cos(xzTheta);
        double vz = vxz * Math.sin(xzTheta);
        @Nullable TurretArrowStack removedArrow = removeArrow();
        if (removedArrow == null) return;
        tickBowDurability();
        setDirty();
        @Nullable EntityType arrowEntity = removedArrow.toEntityType();
        if (arrowEntity == null) {
            return;
        }
        @NotNull Entity entity = spawnLocation.getWorld().spawnEntity(spawnLocation, arrowEntity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        entity.setVelocity(new Vector(vx, vy, vz));
        if (entity instanceof Egg || entity instanceof Snowball) {
            return;
        }
        if (entity instanceof CraftArrow arrow) {
            final NBTTagCompound nbt = removedArrow.getEntityNbt();
            if (nbt != null)
                arrow.getHandle().loadData(nbt);
            if (this.bow.isCrossBow()) {
                arrow.setShotFromCrossbow(true);
            }
            arrow.setDamage(getArrowDamage(arrow));
            arrow.setKnockbackStrength(EnchantmentUtils.knockback(this.bow.getEnchantmentLevel(Enchantment.ARROW_KNOCKBACK)));
            arrow.setFireTicks(EnchantmentUtils.flame(this.bow.getEnchantmentLevel(Enchantment.ARROW_FIRE)));
            arrow.setPierceLevel(this.bow.getEnchantmentLevel(Enchantment.PIERCING));
            arrow.addScoreboardTag("no_stick");
            arrow.addScoreboardTag("no_invincibility_mobs");
        }
    }

    private void tickBowDurability() {
        bow.tickBowDurability();
        correctBowEntity();
    }

    @Nullable
    private TurretArrowStack removeArrow() {
        TurretArrowStack arrow = bow.removeArrow();
        correctRefilledEntity();
        return arrow;
    }

    private void setDirty() {
        this.isDirty = true;
        updateGui();
    }

    private double getArrowDamage(CraftArrow arrow) {
//        EnchantmentUtils.damage(BASE_TURRET_DAMAGE, this.bow.getEnchantmentLevel(Enchantment.ARROW_DAMAGE)));
        return getConfig().getDefaultDamage();
    }

    private boolean noBow() {
        return !this.bow.exists();
    }

    public void repair(int repairAmount) {
        this.health = Math.min(getConfig().getMaxHealth(), repairAmount * getConfig().getHealthPerRepair() + health);
        setDirty();
    }

    public void rotateCenter(double degrees) {
        final Vector direction = turretModel.location.getDirection();
        turretModel.location.setDirection(VectorUtils.rotateVector(direction.getX(), direction.getZ(), direction.getY(), degrees));
        resetRotate();
        setDirty();
    }

    private double velocity(double distanceToTarget) {
        //  20     distance
        // ---- = ----------
        //   V         ?
        // v*distance/20;
        return Math.max(getConfig().getShotSpeed(), getConfig().getShotSpeed() * distanceToTarget / 20);//20  ticks per second
    }

    @Override
    public int hashCode() {
        return this.uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TurretMob other && this.uuid.equals(other.uuid);
    }

    public boolean isDead() {
        Entity main = this.turretModel.getMain();
        if (main == null || main.isDead()) this.isDead = true;
        return isDead;
    }

    public int getRepairCost() {
        return (int) Math.ceil((getConfig().getMaxHealth() - health) / getConfig().getHealthPerRepair());
    }

    public int getRepairAmount() {
        return getConfig().getHealthPerRepair();
    }

    public List<ItemStack> getArrowItems() {
        return this.bow.getArrows();
    }

    @Nullable
    public ItemStack removeArrowSlot(int slot) {
        ItemStack itemStack = this.bow.removeArrowSlot(slot);
        setDirty();
        return itemStack;
    }

    public ItemStack getBowItem() {
        return this.bow.getBowItem();
    }

    @Nullable
    public ItemStack removeBowSlot() {
        ItemStack itemStack = this.bow.removeBowItem();
        setDirty();
        return itemStack;
    }

    public double getHealth() {
        return this.health;
    }

    public Material getRepairMaterial() {
        return Material.IRON_INGOT;
    }

    @Nullable
    public ItemStack addArrow(ItemStack item) {
        ItemStack itemStack = this.bow.addArrow(item);
        setDirty();
        return itemStack;
    }

    @Nullable
    public ItemStack addBow(ItemStack item) {
        ItemStack itemStack = this.bow.addBow(item);
        setDirty();
        return itemStack;
    }

    public abstract TurretTypeIdentifier getTypeIdentifier();
}
