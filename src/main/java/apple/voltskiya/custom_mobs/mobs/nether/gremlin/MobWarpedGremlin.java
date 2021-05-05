package apple.voltskiya.custom_mobs.mobs.nether.gremlin;

import apple.voltskiya.custom_mobs.mobs.PluginNmsMobs;
import apple.voltskiya.custom_mobs.mobs.SpawnCustomMobListener;
import apple.voltskiya.custom_mobs.mobs.parts.MobPartChild;
import apple.voltskiya.custom_mobs.mobs.parts.MobPartMother;
import apple.voltskiya.custom_mobs.mobs.parts.NmsModelConfig;
import apple.voltskiya.custom_mobs.mobs.parts.NmsModelConfig.ModelConfigName;
import apple.voltskiya.custom_mobs.mobs.parts.NmsModelEntityConfig;
import apple.voltskiya.custom_mobs.mobs.utils.UtilsPacket;
import com.mojang.datafixers.types.Type;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftZombie;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class MobWarpedGremlin extends EntityZombie {
    public static final ModelConfigName REGISTERED_MODEL = ModelConfigName.WARPED_GREMLIN;
    public static final String REGISTERED_NAME = REGISTERED_MODEL.getFile();
    private static EntityTypes<MobWarpedGremlin> warpedGremlinEntityType;
    private static NmsModelEntityConfig selfModel;
    private List<MobPartChild> children = null;
    private AttributeMapBase attributeMap = null;

    /**
     * constructor to match the EntityTypes requirement
     *
     * @param world the world to spawn the entity in
     */

    public MobWarpedGremlin(EntityTypes<MobWarpedGremlin> entityTypes, World world) {
        super(entityTypes, world);
    }

    /**
     * registers the WarpedGremlin as an entity
     */
    public static void initialize() {
        Map<? super Object, Type<?>> types = PluginNmsMobs.getMinecraftTypes();
        final Type<?> zombieType = types.get("minecraft:zombie");
        types.put(registeredNameId(), zombieType);

        // build it
        EntityTypes.Builder<MobWarpedGremlin> entitytypesBuilder = EntityTypes.Builder.a(MobWarpedGremlin::new, EnumCreatureType.MONSTER);
        warpedGremlinEntityType = entitytypesBuilder.a(REGISTERED_NAME);
        warpedGremlinEntityType = IRegistry.a(IRegistry.ENTITY_TYPE, IRegistry.ENTITY_TYPE.a(EntityTypes.ZOMBIE), REGISTERED_NAME, warpedGremlinEntityType); // this is good

        // log it
        PluginNmsMobs.get().log(Level.INFO, "registered " + registeredNameId());
        final NmsModelConfig model = NmsModelConfig.parts(REGISTERED_MODEL);
        selfModel = model.mainPart();
    }

    /**
     * spawns a WarpedGremlin
     *
     * @param location the org.bukkit location where the mob should be spawned
     * @param oldNbt   the nbt of the previously spawned mob or null if no entity existed
     */
    public static void spawn(Location location, NBTTagCompound oldNbt) {
        CraftWorld world = (CraftWorld) location.getWorld();
        final MobWarpedGremlin gremlin = new MobWarpedGremlin(warpedGremlinEntityType, world.getHandle());
        gremlin.prepare(location, oldNbt);
        gremlin.addScoreboardTag(SpawnCustomMobListener.CUSTOM_SPAWN_COMPLETE_TAG);
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
        ((CraftZombie) getBukkitEntity()).setInvisible(invisible);
        this.setInvisible(invisible);
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        NBTTagCompound data = super.save(nbttagcompound);
        data.setString("id", registeredNameId());
        return data;
    }

    @NotNull
    private static String registeredNameId() {
        return "minecraft" + ":" + REGISTERED_NAME;
    }


    @Override
    public void die() {
        super.die();
        for (MobPartChild child : children) {
            child.die();
        }
    }


    @Override
    public AttributeMapBase getAttributeMap() {
        return this.attributeMap == null ? this.attributeMap = new AttributeMapBase(getAttributeProvider()) : this.attributeMap;
    }

    /**
     * @return the default attributeMap
     */
    private static AttributeProvider getAttributeProvider() {
        return EntityLiving.cL()
                .a(GenericAttributes.FOLLOW_RANGE, 35.0D)
                .a(GenericAttributes.MOVEMENT_SPEED, 0.23000000417232513D)
                .a(GenericAttributes.ATTACK_DAMAGE, 3.0D)
                .a(GenericAttributes.ARMOR, 2.0D)
                .a(GenericAttributes.SPAWN_REINFORCEMENTS)
                .a(GenericAttributes.ATTACK_KNOCKBACK, 1D)
                .a();
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
        UtilsPacket.sendPacketsToAllPlayers(packetsToSend, this.getBukkitEntity().getLocation());
    }


    @Override
    public boolean isFireProof() {
        return true;
    }

    /**
     * change worlds
     */
    @Override
    public @Nullable
    Entity b(WorldServer worldserver) {
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
