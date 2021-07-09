package apple.voltskiya.custom_mobs.turrets.mobs;

import apple.voltskiya.custom_mobs.old_turrets.OldTurretMob;
import apple.voltskiya.custom_mobs.sql.DBItemStack;
import apple.voltskiya.custom_mobs.turrets.TurretList;
import apple.voltskiya.custom_mobs.turrets.TurretMobSaveable;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftArrow;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.utilities.util.DistanceUtils;
import voltskiya.apple.utilities.util.EntityLocation;
import voltskiya.apple.utilities.util.VectorUtils;
import voltskiya.apple.utilities.util.gui.InventoryGui;
import voltskiya.apple.utilities.util.minecraft.EnchantmentUtils;

import java.util.*;

public abstract class TurretMob implements Runnable {
    public static final int ARROW_SLOT = 3;
    protected static final int MAX_HEALTH = 200;
    protected static final double VELOCITY = 7.0; // velocity of the arrow
    protected static final int MAX_TARGET_RECORDING = 5;
    protected static final double HEALTH_PER_REPAIR = 10;
    protected static final double GRAVITY = -1.0; // gravity
    protected static final double BASE_TURRET_DAMAGE = 2;
    protected final String typeId;
    protected final List<Vector> targetLastLocation = new ArrayList<>();
    protected List<Entity> entities = new ArrayList<>();
    protected Set<UUID> entitiesUUIDs = new HashSet<>();
    protected Entity bowEntity;
    protected Entity refilledEntity;
    protected Entity durabilityEntity;
    protected double health = MAX_HEALTH;
    protected List<DBItemStack> arrows = new ArrayList<>();
    protected LivingEntity target = null;
    protected int tickIndex = 0;
    protected Location center;
    protected Vector facing;
    protected List<EntityLocation> entitiesLocations = new ArrayList<>();
    private int bowDurability;
    private Material bowMaterial = null;
    private HashMap<Enchantment, Integer> bowEnchantments = new HashMap<>();

    protected TurretMob(String typeId) {
        this.typeId = typeId;
        for (int i = 0; i < ARROW_SLOT; i++)
            arrows.add(new DBItemStack(null, 0, new NBTTagCompound().asString()));
    }

    public TurretMob(String typeId, TurretMobSaveable saveable) {
        this.typeId = typeId;
        for (UUID entity : saveable.getEntities()) {
            entities.add(Bukkit.getEntity(entity));
            entitiesUUIDs.add(entity);
        }
        entities.removeIf(Objects::isNull);
        bowEntity = Bukkit.getEntity(saveable.getBowEntity());
        refilledEntity = Bukkit.getEntity(saveable.getRefilledEntity());
        durabilityEntity = Bukkit.getEntity(saveable.getDurabilityEntity());
        this.center = saveable.getCenter();
        this.health = saveable.getHealth();
        this.arrows = saveable.getArrows();
        for (int i = arrows.size(); i < ARROW_SLOT; i++)
            arrows.add(new DBItemStack(null, 0, new NBTTagCompound().asString()));
        this.facing = saveable.getFacing();
        this.entitiesLocations = saveable.getEntitiesLocations();
        this.bowEnchantments = saveable.getBowEnchantments();
        this.bowMaterial = saveable.getBowMaterial();
        this.bowDurability = saveable.getBowDurability();
    }

    public static String getTypeIdFieldName() {
        return "typeId";
    }

    public String getTypeId() {
        return typeId;
    }

    public void addEntity(Entity spawned) {
        this.entities.add(spawned);
        this.entitiesUUIDs.add(spawned.getUniqueId());
        this.entitiesLocations.add(new EntityLocation(spawned,
                -this.center.getX(), -this.center.getY(), -this.center.getZ()));
        spawned.addScoreboardTag(OldTurretMob.TURRET_TAG);
    }

    public void addBowEntity(Entity spawned) {
        this.bowEntity = spawned;
        this.addEntity(spawned);
    }

    public void addRefilledEntity(Entity spawned) {
        this.refilledEntity = spawned;
        this.addEntity(spawned);
    }

    public void addDurabilityEntity(Entity spawned) {
        this.durabilityEntity = spawned;
        this.addEntity(spawned);
    }

    public synchronized void damage(double damage) {
        this.health -= damage;
        if (health <= 0) {
            remove();
        }
        correctDurabilityEntity();
    }

    private void correctDurabilityEntity() {
        if (durabilityEntity instanceof ArmorStand) {
            final EntityEquipment equipment = ((ArmorStand) durabilityEntity).getEquipment();
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
        if (this.refilledEntity instanceof ArmorStand) {
            final EntityEquipment equipment = ((ArmorStand) refilledEntity).getEquipment();
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
        if (this.bowEntity instanceof ArmorStand) {
            final EntityEquipment equipment = ((ArmorStand) bowEntity).getEquipment();
            if (equipment != null) {
                if (noBow()) {
                    final ItemStack torch = new ItemStack(Material.AIR);
                    equipment.setHelmet(torch);
                } else {
                    Material material = getBowMaterial();
                    equipment.setHelmet(material == null ? null : new ItemStack(material));
                }
            }
        }
    }

    protected void remove() {
        removeEntities();
        TurretList.remove(this);
    }

    private void removeEntities() {
        for (Entity entity : entities) {
            if (entity != null) entity.remove();
        }
        if (bowEntity != null) bowEntity.remove();
        if (refilledEntity != null) refilledEntity.remove();
        if (durabilityEntity != null) durabilityEntity.remove();
    }

    private boolean noBow() {
        if (getBowMaterial() == null || getBowMaterial().isAir()) return true;
        return getBowDurability() <= 0;
    }


    private boolean arrowsEmpty() {
        for (DBItemStack arrow : this.arrows) {
            if (arrow.exists()) return false;
        }
        return true;
    }

    public String getUUID() {
        return durabilityEntity.getUniqueId().toString();
    }

    public void run() {
        if (this.durabilityEntity.isDead()) {
            remove();
            return;
        }
        if (target == null) {
            retarget();
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

    private void shoot(LivingEntity target) {
        if (arrowsEmpty() || noBow()) return;
        Location goal = target.getEyeLocation();
        int lastLocationSize = this.targetLastLocation.size();
        while (lastLocationSize > MAX_TARGET_RECORDING) {
            this.targetLastLocation.remove(0);
            lastLocationSize--;
        }
        double distanceToTarget = DistanceUtils.distance(goal, getCenter());

        if (distanceToTarget < getMaxSight() && distanceToTarget > getMinSight()) {
            Location spawnLocation = getCenter().clone();
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
                    if (getBowMaterial() == Material.CROSSBOW) {
                        arrow.setShotFromCrossbow(true);
                    }
                    arrow.setDamage(getDamage());
                    arrow.setKnockbackStrength(getKnockback());
                    arrow.setFireTicks(getFlame());
                    arrow.setPierceLevel(getEnchantmentLevel(Enchantment.PIERCING));
                    arrow.addScoreboardTag("no_stick");
                    arrow.addScoreboardTag("no_invincibility_mobs");
                });
            }
        }
    }

    protected void tickBowDurability() {
        int unbreaking = getEnchantmentLevel(Enchantment.DURABILITY);
        boolean doBreak = EnchantmentUtils.randomBreakUnbreaking(unbreaking);
        if (doBreak) {
            if (--this.bowDurability <= 0) {
                // delete bow
                this.bowMaterial = null;
                this.correctBowEntity();
            }
        }

    }

    private int getFlame() {
        return EnchantmentUtils.flame(getEnchantmentLevel(Enchantment.ARROW_FIRE));
    }

    private int getKnockback() {
        return EnchantmentUtils.knockback(getEnchantmentLevel(Enchantment.ARROW_KNOCKBACK));
    }

    private double getDamage() {
        return EnchantmentUtils.damage(BASE_TURRET_DAMAGE, getEnchantmentLevel(Enchantment.ARROW_DAMAGE));
    }

    public Location getCenter() {
        return center;
    }

    public void setCenter(Location location) {
        this.center = location;
    }

    protected DBItemStack removeArrow() {
        for (DBItemStack arrow : arrows) {
            final int count = arrow.count;
            if (arrow.type != Material.AIR && count > 0) {
                if (count <= 1) {
                    arrow.type = Material.AIR;
                } else {
                    arrow.count--;
                }
                correctRefilledEntity();
                updateGui();
                TurretList.registerOrUpdate(this);
                return arrow;
            }
        }
        return null;
    }

    public void repair(int repairAmount) {
        this.health = Math.min(MAX_HEALTH, repairAmount * HEALTH_PER_REPAIR + health);
    }

    private double velocity(double distanceToTarget) {
        //  20     distance
        // ---- = ----------
        //   V         ?
        // v*distance/20;
        return Math.max(VELOCITY, VELOCITY * distanceToTarget / 15);
    }

    public double getMinSight() {
        return 3.5;
    }

    protected double getMaxSight() {
        return 50;
    }

    /**
     * @return how many skipped ticks before shooting
     */
    protected int ticksPerShoot() {
        if (this.getBowMaterial() == null) return 1;
        final int quickCharge = getEnchantmentLevel(Enchantment.QUICK_CHARGE);
        return switch (quickCharge) {
            case 0 -> 4;
            case 1 -> 3;
            case 2 -> 2;
            default -> 1;
        };
    }

    protected boolean shouldTarget(@Nullable Entity entity) {
        if (entity instanceof LivingEntity alive) {
            double distance = DistanceUtils.distance(entity.getLocation(), center);
            if (!alive.isDead() && alive.hasLineOfSight(durabilityEntity) && distance < getMaxSight() && distance > getMinSight()) {
                final Vector newFacing = entity.getLocation().subtract(center).toVector().setY(0).normalize();
                return rotate(newFacing);
            }
        }
        return false;
    }

    public void resetRotate() {
        rotate(center.getDirection());
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
        if (angle < getMaxAngle() || Math.abs(Math.PI * 2 - angle) < getMaxAngle()) {
            // rotate by "angleN" degrees
            for (EntityLocation entity : entitiesLocations) {
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

    private double getMaxAngle() {
        return Math.toRadians(90);
    }

    protected void retarget() {
        // if we shouldn't target, don't target
        for (Entity entity : this.durabilityEntity.getNearbyEntities(getMaxSight(), getMaxSight(), getMaxSight())) {
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
    }

    public abstract TurretMobSaveable toSaveable();

    public List<Entity> getEntities() {
        return entities;
    }

    public Entity getBowEntity() {
        return bowEntity;
    }

    public Entity getRefilledEntity() {
        return refilledEntity;
    }

    public Entity getDurabilityEntity() {
        return durabilityEntity;
    }

    public double getHealth() {
        return health;
    }

    public List<DBItemStack> getArrows() {
        return arrows;
    }

    public Vector getFacing() {
        return facing;
    }

    public List<EntityLocation> getEntitiesLocations() {
        return entitiesLocations;
    }

    private int getEnchantmentLevel(Enchantment piercing) {
        return this.bowEnchantments.getOrDefault(piercing, 0);
    }

    public HashMap<Enchantment, Integer> getEnchantmentLevels() {
        return this.bowEnchantments;
    }

    @Nullable
    public Material getBowMaterial() {
        return bowMaterial;
    }

    public int getBowDurability() {
        return bowDurability;
    }

    public ItemStack getBowItem() {
        if (bowMaterial == null) return null;
        ItemStack item = new ItemStack(bowMaterial);
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta instanceof Damageable damageable) {
            damageable.setDamage(bowMaterial.getMaxDurability() - bowDurability);
        }
        for (Map.Entry<Enchantment, Integer> enchantment : bowEnchantments.entrySet()) {
            itemMeta.addEnchant(enchantment.getKey(), enchantment.getValue(), false);
        }
        item.setItemMeta(itemMeta);
        return item;
    }

    public void setBow(@Nullable ItemStack item) {
        if (item == null) {
            bowMaterial = null;
            bowEnchantments = new HashMap<>();
        } else {
            bowMaterial = item.getType();
            bowEnchantments = new HashMap<>(item.getEnchantments());
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta instanceof Damageable damageable) {
                bowDurability = bowMaterial.getMaxDurability() - damageable.getDamage();
            } else {
                bowDurability = 1;
            }
        }
        TurretList.registerOrUpdate(this);
        updateGui();
    }

    public boolean interact(Player player, Entity entity) {
        if (entitiesUUIDs.contains(entity.getUniqueId())) {
            player.openInventory(getTurretGui(player).getInventory());
            return true;
        }
        return false;
    }

    protected abstract InventoryGui getTurretGui(Player player);

    protected abstract void updateGui();

    public boolean verifyAlive() {
        if (this.durabilityEntity == null || this.bowEntity == null || this.refilledEntity == null) {
            removeEntities();
            return false;
        }
        return true;
    }

    public void addArrows(ItemStack item) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(item);
        @javax.annotation.Nullable NBTTagCompound nbt = nmsItemStack.getTag();
        for (DBItemStack arrow : getArrows()) {
            if (!arrow.exists()) {
                arrow.type = item.getType();
                arrow.count = item.getAmount();
                arrow.nbt = nmsItemStack.save(new NBTTagCompound()).asString();
                item.setAmount(0);
                TurretList.registerOrUpdate(this);
                updateGui();
                return;
            } else if (arrow.type == item.getType() && (
                    (nbt == null && arrow.getEntityNbt() == null) ||
                            (nbt != null && nbt.equals(arrow.getEntityNbt())))) {
                int arrowToMerge = arrow.type.getMaxStackSize() - arrow.count;
                arrowToMerge = Math.min(arrowToMerge, item.getAmount());
                item.setAmount(item.getAmount() - arrowToMerge);
                arrow.count += arrowToMerge;
                if (item.getAmount() == 0) {
                    TurretList.registerOrUpdate(this);
                    updateGui();
                    return;
                }
            }
        }
        TurretList.registerOrUpdate(this);
        updateGui();
    }

    @Nullable
    public ItemStack removeArrowAt(int index) {
        if (arrows.size() <= index) return null;
        DBItemStack arrow = arrows.get(index);
        ItemStack arrowItem = arrow.toItem();
        arrow.count = 0;
        arrow.type = null;
        arrow.nbt = null;
        TurretList.registerOrUpdate(this);
        updateGui();
        return arrowItem;
    }
}
