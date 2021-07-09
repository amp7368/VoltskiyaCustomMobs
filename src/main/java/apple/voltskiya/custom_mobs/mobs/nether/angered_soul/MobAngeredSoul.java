package apple.voltskiya.custom_mobs.mobs.nether.angered_soul;

import apple.nms.decoding.attribute.DecodeGenericAttributes;
import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.entity.DecodeEnumCreatureType;
import apple.nms.decoding.iregistry.DecodeDamageSource;
import apple.nms.decoding.iregistry.DecodeEntityTypes;
import apple.nms.decoding.iregistry.DecodeIRegistry;
import apple.nms.decoding.pathfinder.DecodeControllerMoveFlying;
import apple.voltskiya.custom_mobs.mobs.PluginNmsMobs;
import apple.voltskiya.custom_mobs.mobs.RegisteredCustomMob;
import apple.voltskiya.custom_mobs.mobs.SpawnCustomMobListener;
import apple.voltskiya.custom_mobs.mobs.target_selector.PathfinderGoalClosestPlayer;
import apple.voltskiya.custom_mobs.pathfinders.PathfinderGoalApproachSlowly;
import com.mojang.datafixers.types.Type;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSelector;
import net.minecraft.world.entity.ai.navigation.NavigationFlying;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.monster.EntitySkeleton;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftSkeleton;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class MobAngeredSoul extends EntitySkeleton implements RegisteredCustomMob {
    public static final String REGISTERED_NAME = "angered_soul";
    private static final double SIGHT = 100;
    private static EntityTypes<MobAngeredSoul> entityTypes;
    private AttributeMapBase attributeMap = null;

    /**
     * registers the EyePlant as an entity
     */
    public static void initialize() {
        Map<? super Object, Type<?>> types = PluginNmsMobs.getMinecraftTypes();
        final Type<?> oldType = types.get("minecraft:skeleton");
        types.put(registeredNameId(), oldType);

        // build it
        EntityTypes.Builder<MobAngeredSoul> entitytypesBuilder = EntityTypes.Builder.a(MobAngeredSoul::new, DecodeEnumCreatureType.MONSTER.encode());
        entityTypes = entitytypesBuilder.a(REGISTERED_NAME);
        entityTypes = IRegistry.a(DecodeIRegistry.getEntityType(), DecodeIRegistry.getEntityType().getId(DecodeEntityTypes.SKELETON), REGISTERED_NAME, entityTypes);
        // log it
        PluginNmsMobs.get().log(Level.INFO, "registered " + registeredNameId());
    }

    /**
     * constructor to match the EntityTypes requirement
     *
     * @param world the world to spawn the entity in
     */

    public MobAngeredSoul(EntityTypes<MobAngeredSoul> entityTypes, World world) {
        super(DecodeEntityTypes.SKELETON, world);
    }


    /**
     * spawns a WarpedGremlin
     *
     * @param location the org.bukkit location where the mob should be spawned
     * @param oldNbt   the nbt of the previously spawned mob or null if no entity existed
     */
    public static void spawn(Location location, @Nullable NBTTagCompound oldNbt) {
        CraftWorld world = (CraftWorld) location.getWorld();
        final MobAngeredSoul mob = new MobAngeredSoul(entityTypes, world.getHandle());
        mob.prepare(location, oldNbt);
        mob.addScoreboardTag(SpawnCustomMobListener.CUSTOM_SPAWN_COMPLETE_TAG);
        mob.addScoreboardTag(REGISTERED_NAME);
        world.getHandle().addEntity(mob);
    }

    public static void spawnEat(CreatureSpawnEvent event) {
        Location location = event.getEntity().getLocation();
        spawn(location, ((CraftEntity) event.getEntity()).getHandle().save(new NBTTagCompound()));
        event.setCancelled(true);
    }

    private void prepare(Location location, NBTTagCompound oldNbt) {
        if (oldNbt != null)
            this.load(oldNbt);
        this.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        nbttagcompound.setString("id", registeredNameId());
        super.load(nbttagcompound);
        final boolean invisible = nbttagcompound.getBoolean("Invisible");
        ((CraftSkeleton) this.getBukkitEntity()).setInvisible(invisible);
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        NBTTagCompound data = super.save(nbttagcompound);
        data.setBoolean("Invisible", this.isInvisible());
        data.setString("id", registeredNameId());
        return data;
    }


    @NotNull
    private static String registeredNameId() {
        return "minecraft" + ":" + REGISTERED_NAME;
    }

    @Override
    public AttributeMapBase getAttributeMap() {
        return this.attributeMap == null ? this.attributeMap = new AttributeMapBase(getAttributeProvider()) : this.attributeMap;
    }

    @Override
    protected void checkBlockCollisions() {
        // never collide
    }

    @Override
    public void collide(Entity entity) {
        super.collide(entity);
        if (entity instanceof EntityHuman) {
            this.explode();
        }
    }

    public void explode() {
        List<org.bukkit.entity.Entity> nearbyEntities = this.getBukkitEntity().getNearbyEntities(1.5, 1.5, 1.5);
        for (org.bukkit.entity.Entity nearby : nearbyEntities) {
            if (nearby instanceof LivingEntity) {
                final EntityLiving handle = ((CraftLivingEntity) nearby).getHandle();
                if (handle != this)
                    if (!handle.isBlocking()) {
                        if (nearby instanceof Player player) {
                            if (player.getGameMode() == GameMode.SURVIVAL)
                                handle.damageEntity(DecodeDamageSource.MAGIC, 10f);
                        } else {
                            handle.damageEntity(DecodeDamageSource.MAGIC, 10f);
                        }
                    }
            }
            final Location location = this.getBukkitEntity().getLocation();
            location.getWorld().spawnParticle(org.bukkit.Particle.EXPLOSION_LARGE, location, 1);

        }
        this.die();
    }

    public AttributeProvider getAttributeProvider() {
        return EntityMonster.fA()
                .a(DecodeGenericAttributes.MOVEMENT_SPEED, 0.25D)
                .a(DecodeGenericAttributes.FLYING_SPEED, .5d)
                .a(DecodeGenericAttributes.FOLLOW_RANGE, 100)
                .a();
    }

    @Override
    public EntityTypes<?> getEntityType() {
        return entityTypes;
    }

    @Override
    public boolean isOnGround() {
        return false;
    }

    @Override
    public boolean isInvulnerable(DamageSource damagesource) {
        return damagesource == DecodeDamageSource.FALL || super.isInvulnerable(damagesource);
    }

    @Override
    protected void initPathfinder() {
        //navigation
        this.bN = new NavigationFlying(this, getWorld());
        this.bL = new ControllerMoveGhost(this, 1d); // no gravity true
        PathfinderGoalSelector goalSelector = DecodeEntity.getGoalSelector(this);
        PathfinderGoalSelector targetSelector = DecodeEntity.getTargetSelector(this);
        goalSelector.a(1, new PathfinderGoalApproachSlowly(this, 1, 10, new AngeredSoulScream(this)));
        goalSelector.a(0, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 1));
        targetSelector.a(1, new PathfinderGoalClosestPlayer(this, SIGHT, true));
    }

    @Override
    public void t() {
        // do no special pathfinding
    }

    private static class ControllerMoveGhost extends DecodeControllerMoveFlying {
        public ControllerMoveGhost(EntityInsentient me, double speed) {
            // me, speed, noGravity
            super(me, (int) speed, true);
            me.setNoGravity(true);
        }

        public void a() {
            super.a();
            // check we're moving instead of jumping or whatever
            if (this.k == DecodeOperation.MOVE_TO.encode()) {
                // set the y change to be a smooth ascent or descent
                double var2 = y() - d.locY();
                // change y value to what it should be rather than oscillating with the target
                DecodeEntity.setYMove(me(), (float) var2);
            }
        }
    }
}
