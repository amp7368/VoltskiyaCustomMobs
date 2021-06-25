package apple.voltskiya.custom_mobs.mobs.misc;

import apple.nms.decoding.attribute.DecodeGenericAttributes;
import apple.nms.decoding.entity.DecodeEnumCreatureType;
import apple.nms.decoding.iregistry.DecodeEntityTypes;
import apple.nms.decoding.iregistry.DecodeIRegistry;
import apple.voltskiya.custom_mobs.mobs.PluginNmsMobs;
import apple.voltskiya.custom_mobs.mobs.RegisteredCustomMob;
import apple.voltskiya.custom_mobs.mobs.SpawnCustomMobListener;
import com.mojang.datafixers.types.Type;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftZombie;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.logging.Level;

public class MobHealthPack extends EntityZombie implements RegisteredCustomMob {
    public static final String REGISTERED_NAME = "health_pack";

    private static EntityTypes<MobHealthPack> entityTypes;

    /**
     * registers the EyePlant as an entity
     */
    public static void initialize() {
        Map<? super Object, Type<?>> types = PluginNmsMobs.getMinecraftTypes();
        final Type<?> oldType = types.get("minecraft:zombie");
        types.put(registeredNameId(), oldType);

        // build it
        EntityTypes.Builder<MobHealthPack> entitytypesBuilder = EntityTypes.Builder.a(MobHealthPack::new, DecodeEnumCreatureType.MONSTER.encode());
        entityTypes = entitytypesBuilder.a(REGISTERED_NAME);
        entityTypes = IRegistry.a(DecodeIRegistry.getEntityType(), DecodeIRegistry.getEntityType().getId(DecodeEntityTypes.ZOMBIE), REGISTERED_NAME, entityTypes);
        // log it
        PluginNmsMobs.get().log(Level.INFO, "registered " + registeredNameId());
    }

    @NotNull
    private static String registeredNameId() {
        return "minecraft" + ":" + REGISTERED_NAME;
    }


    /**
     * constructor to match the EntityTypes requirement
     *
     * @param world the world to spawn the entity in
     */

    public MobHealthPack(EntityTypes<MobHealthPack> entityTypes, World world) {
        super(DecodeEntityTypes.ZOMBIE, world);
    }

    public static void spawnEat(CreatureSpawnEvent event) {
        Location location = event.getEntity().getLocation();
        spawn(location, ((CraftEntity) event.getEntity()).getHandle().save(new NBTTagCompound()));
        event.setCancelled(true);
    }

    /**
     * spawns a WarpedGremlin
     *
     * @param location the org.bukkit location where the mob should be spawned
     * @param oldNbt   the nbt of the previously spawned mob or null if no entity existed
     */
    public static void spawn(Location location, @Nullable NBTTagCompound oldNbt) {
        CraftWorld world = (CraftWorld) location.getWorld();
        final MobHealthPack mob = new MobHealthPack(entityTypes, world.getHandle());
        mob.prepare(location, oldNbt);
        mob.addScoreboardTag(SpawnCustomMobListener.CUSTOM_SPAWN_COMPLETE_TAG);
        mob.addScoreboardTag(REGISTERED_NAME);
        world.getHandle().addEntity(mob);
    }

    private void prepare(Location location, NBTTagCompound oldNbt) {
        if (oldNbt != null)
            this.load(oldNbt);
        this.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

    }

    @Override
    public void collide(Entity entity) {
        if (entity instanceof EntityHuman) {
            this.healPlayer((EntityHuman) entity);
            die();
        }
    }

    private void healPlayer(EntityHuman player) {
        final AttributeModifiable health = getAttributeInstance(DecodeGenericAttributes.MAX_HEALTH);
        player.heal((float) (health == null ? 1f : health.getValue()));
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
    protected void checkBlockCollisions() {
        // never collide
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        NBTTagCompound data = super.save(nbttagcompound);
        data.setBoolean("Invisible", this.isInvisible());
        data.setString("id", registeredNameId());
        return data;
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        nbttagcompound.setString("id", registeredNameId());
        super.load(nbttagcompound);
        ((CraftZombie) this.getBukkitEntity()).setInvisible(true);
        ((CraftZombie) this.getBukkitEntity()).setBaby(true);
    }

    @Override
    public boolean isInvulnerable(DamageSource damagesource) {
        return true;
    }

    @Override
    protected void initPathfinder() {
    }
}
