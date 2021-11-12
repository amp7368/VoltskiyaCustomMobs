package apple.voltskiya.custom_mobs.mobs.nms.nether.gremlin;

import apple.nms.decoding.entity.DecodeEnumCreatureType;
import apple.nms.decoding.iregistry.DecodeEntityTypes;
import apple.nms.decoding.iregistry.DecodeIRegistry;
import apple.voltskiya.custom_mobs.mobs.PluginNmsMobs;
import apple.voltskiya.custom_mobs.mobs.SpawnCustomMobListener;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobRegister;
import apple.voltskiya.custom_mobs.mobs.nms.parent.register.RegisteredCustomMob;
import apple.voltskiya.custom_mobs.mobs.nms.parts.MobPartMother;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModelConfig;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModelConfig.ModelConfigName;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModelEntityConfig;
import apple.voltskiya.custom_mobs.mobs.nms.parts.child.MobPartChild;
import apple.voltskiya.custom_mobs.mobs.nms.utils.UtilsPacket;
import com.mojang.datafixers.types.Type;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.attributes.AttributeDefaults;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.monster.EntityZombie;
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

public class MobWarpedGremlin extends EntityZombie implements RegisteredCustomMob {
    public static final ModelConfigName REGISTERED_MODEL = ModelConfigName.WARPED_GREMLIN;
    public static final String REGISTERED_NAME = REGISTERED_MODEL.getName();
    private static EntityTypes<MobWarpedGremlin> entityTypes;
    private static NmsModelEntityConfig selfModel;
    private List<MobPartChild> children = null;
    private AttributeMapBase attributeMap = null;

    /**
     * registers the WarpedGremlin as an entity
     */
    public static void initialize() {
        Map<? super Object, Type<?>> types = NmsMobRegister.getMinecraftTypes();
        final Type<?> zombieType = types.get("minecraft:zombie");
        types.put(registeredNameId(), zombieType);

        // build it
        EntityTypes.Builder<MobWarpedGremlin> entitytypesBuilder = EntityTypes.Builder.a(MobWarpedGremlin::new, DecodeEnumCreatureType.MONSTER.encode());
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

    public MobWarpedGremlin(EntityTypes<MobWarpedGremlin> entityTypes, World world) {
        super(DecodeEntityTypes.ZOMBIE, world);
    }

    @Override
    public EntityTypes<?> getEntityType() {
        return entityTypes;
    }

    /**
     * spawns a WarpedGremlin
     *
     * @param location the org.bukkit location where the mob should be spawned
     * @param oldNbt   the nbt of the previously spawned mob or null if no entity existed
     */
    public static void spawn(Location location, NBTTagCompound oldNbt) {
        CraftWorld world = (CraftWorld) location.getWorld();
        final MobWarpedGremlin gremlin = new MobWarpedGremlin(entityTypes, world.getHandle());
        gremlin.prepare(location, oldNbt);
        gremlin.addScoreboardTag(SpawnCustomMobListener.CUSTOM_SPAWN_COMPLETE_TAG);
        gremlin.addScoreboardTag(REGISTERED_NAME);
        world.getHandle().addEntity(gremlin);
        gremlin.addChildren();
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
    }


    @Override
    public void load(NBTTagCompound nbttagcompound) {
        nbttagcompound.setString("id", registeredNameId());
        super.load(nbttagcompound);
        final boolean invisible = nbttagcompound.getBoolean("Invisible");
        ((CraftZombie) this.getBukkitEntity()).setInvisible(invisible);
        this.setInvisible(invisible);
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

    /**
     * @return the default attributeMap
     */
    private static AttributeProvider getAttributeProvider() {
        return AttributeDefaults.a(DecodeEntityTypes.ZOMBIE);
    }


    @Override
    public AttributeMapBase getAttributeMap() {
        return this.attributeMap == null ? this.attributeMap = new AttributeMapBase(getAttributeProvider()) : this.attributeMap;
    }

    @Override
    public void a(Entity.RemovalReason removalReason) {
        super.a(removalReason);
        if (this.children != null)
            for (MobPartChild child : children) {
                child.die();
            }
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


    @Override
    public boolean isFireProof() {
        return true;
    }

    /**
     * change worlds
     */
    @Override
    @Nullable
    public Entity b(WorldServer worldserver) {
        final Entity result = super.b(worldserver);
        if (result instanceof MobWarpedGremlin) {
            for (MobPartChild child : children) {
                child.die();
            }
            ((MobWarpedGremlin) result).addChildren();
        }
        return result;
    }
}
