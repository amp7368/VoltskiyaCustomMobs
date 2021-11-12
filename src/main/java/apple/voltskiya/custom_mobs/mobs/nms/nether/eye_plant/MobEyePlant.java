package apple.voltskiya.custom_mobs.mobs.nms.nether.eye_plant;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.entity.DecodeEnumCreatureType;
import apple.nms.decoding.iregistry.DecodeEntityTypes;
import apple.nms.decoding.iregistry.DecodeIRegistry;
import apple.nms.decoding.sound.DecodeSoundEffects;
import apple.voltskiya.custom_mobs.mobs.PluginNmsMobs;
import apple.voltskiya.custom_mobs.mobs.SpawnCustomMobListener;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobRegister;
import apple.voltskiya.custom_mobs.mobs.nms.parent.register.RegisteredCustomMob;
import apple.voltskiya.custom_mobs.mobs.nms.parts.MobPartMother;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModelConfig;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModelEntityConfig;
import apple.voltskiya.custom_mobs.mobs.nms.parts.child.MobPartChild;
import apple.voltskiya.custom_mobs.mobs.nms.parts.child.MobParts;
import apple.voltskiya.custom_mobs.mobs.nms.utils.UtilsPacket;
import com.mojang.datafixers.types.Type;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.attributes.AttributeDefaults;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftZombie;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class MobEyePlant extends EntityZombie implements RegisteredCustomMob {
    public static final NmsModelConfig.ModelConfigName REGISTERED_MODEL = NmsModelConfig.ModelConfigName.EYE_PLANT;
    public static final String REGISTERED_NAME = REGISTERED_MODEL.getName();
    private static EntityTypes<MobEyePlant> entityTypes;
    private static NmsModelEntityConfig selfModel;
    private List<MobPartChild> children = null;
    private AttributeMapBase attributeMap = null;

    /**
     * registers the EyePlant as an entity
     */
    public static void initialize() {
        Map<? super Object, Type<?>> types = NmsMobRegister.getMinecraftTypes();
        final Type<?> zombieType = types.get("minecraft:zombie");
        types.put(registeredNameId(), zombieType);

        // build it
        EntityTypes.Builder<MobEyePlant> entitytypesBuilder = EntityTypes.Builder.a(MobEyePlant::new, DecodeEnumCreatureType.MONSTER.encode());
        entityTypes = entitytypesBuilder.a(REGISTERED_NAME);
        entityTypes = IRegistry.a(DecodeIRegistry.getEntityType(), DecodeIRegistry.getEntityType().getId(DecodeEntityTypes.ZOMBIE), REGISTERED_NAME, entityTypes); // this is good
        // log it
        PluginNmsMobs.get().log(Level.INFO, "registered " + registeredNameId());
        final NmsModelConfig model = NmsModelConfig.parts(REGISTERED_MODEL);
        selfModel = model.mainPart();
    }

    /**
     * constructor to match the EntityTypes requirement
     *
     * @param world the world to spawn the entity in
     */

    public MobEyePlant(EntityTypes<MobEyePlant> entityTypes, World world) {
        super(DecodeEntityTypes.ZOMBIE, world);
    }


    /**
     * spawns a WarpedGremlin
     *
     * @param location the org.bukkit location where the mob should be spawned
     * @param oldNbt   the nbt of the previously spawned mob or null if no entity existed
     */
    public static void spawn(Location location, @Nullable NBTTagCompound oldNbt) {
        CraftWorld world = (CraftWorld) location.getWorld();
        final MobEyePlant eyePlant = new MobEyePlant(entityTypes, world.getHandle());
        eyePlant.prepare(location, oldNbt);
        eyePlant.addScoreboardTag(SpawnCustomMobListener.CUSTOM_SPAWN_COMPLETE_TAG);
        eyePlant.addScoreboardTag(REGISTERED_NAME);
        world.getHandle().addEntity(eyePlant);
        eyePlant.addChildren();
    }

    public static void spawnEat(CreatureSpawnEvent event) {
        Location location = event.getEntity().getLocation();
        spawn(location, ((CraftEntity) event.getEntity()).getHandle().save(new NBTTagCompound()));
        event.setCancelled(true);
    }

    private void prepare(Location location, NBTTagCompound oldNbt) {
        final NBTTagCompound newNbt = selfModel.getEntity().nbt;
        final NBTTagCompound mergedNbt = oldNbt == null ? newNbt : oldNbt.a(newNbt);
        this.load(mergedNbt);
        this.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    private void addChildren() {
        if (this.children != null) {
            for (MobPartChild child : children) {
                child.die();
            }
        }
        this.children = MobPartMother.getChildren(this.getUniqueID(), this, selfModel, REGISTERED_MODEL);
        this.bL = new MobParts.ControllerLookChildrenFollow(this, this.children);
    }

    @Override
    protected void initPathfinder() {
        // only look aat the player
        if (this.children != null) this.bL = new MobParts.ControllerLookChildrenFollow(this, this.children);
        DecodeEntity.getGoalSelector(this).a(0, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
    }

    @Override
    public AttributeMapBase getAttributeMap() {
        return this.attributeMap == null ? this.attributeMap = new AttributeMapBase(getAttributeProvider()) : this.attributeMap;
    }

    @Override
    public boolean isFireProof() {
        return true;
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        nbttagcompound.setString("id", registeredNameId());
        super.load(nbttagcompound);
        final boolean invisible = nbttagcompound.getBoolean("Invisible");
        ((CraftZombie) this.getBukkitEntity()).setInvisible(invisible);
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
    protected SoundEffect getSoundAmbient() {
        if (this.getRandom().nextBoolean()) return null;
        ambientParticles();
        final double choice = this.getRandom().nextDouble();
        if (choice < 1 / 3f) {
            return DecodeSoundEffects.ENTITY_ENDERMAN_AMBIENT;
        } else if (choice < 2 / 3f) {
            return DecodeSoundEffects.BLOCK_ENDER_CHEST_CLOSE;
        } else {
            return DecodeSoundEffects.BLOCK_ENDER_CHEST_OPEN;
        }
    }

    @Override
    public void setOnFire(int i) {

    }

    @Override
    public void move(EnumMoveType enummovetype, Vec3D vec3d) {
        super.move(enummovetype, vec3d);
        List<Packet<?>> packetsToSend = new ArrayList<>();
        if (this.children == null) {
            this.addChildren();
        }
        for (MobPartChild child : children) {
            packetsToSend.add(child.moveFromMother(false));
        }
        UtilsPacket.sendPacketsToNearbyPlayers(packetsToSend, this.getBukkitEntity().getLocation());
    }

    private void ambientParticles() {
        CraftEntity me = getBukkitEntity();
        for (int i = 0; i < 10; i++) {
            Location location = me.getLocation();
            double x = location.getX();
            double y = location.getY();
            double z = location.getZ();
            double xi = this.getRandom().nextDouble() - .5;
            double yi = this.getRandom().nextDouble() - .5;
            double zi = this.getRandom().nextDouble() - .5;
            me.getWorld().spawnParticle(org.bukkit.Particle.CRIT_MAGIC, x + xi, y + yi, z + zi, 1);
        }
    }

    public AttributeProvider getAttributeProvider() {
        return AttributeDefaults.a(DecodeEntityTypes.ZOMBIE);
    }

    @Override
    public EntityTypes<?> getEntityType() {
        return entityTypes;
    }

    @Override
    public void a(Entity.RemovalReason removalReason) {
        super.a(removalReason);
        for (MobPartChild child : children) {
            child.die();
        }
    }
}
