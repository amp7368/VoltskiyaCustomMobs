package apple.voltskiya.custom_mobs.mobs.nms.nether.revenant;

import apple.nms.decoding.attribute.DecodeGenericAttributes;
import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.entity.DecodeEnumCreatureType;
import apple.nms.decoding.iregistry.DecodeEntityTypes;
import apple.nms.decoding.iregistry.DecodeIRegistry;
import apple.voltskiya.custom_mobs.mobs.PluginNmsMobs;
import apple.voltskiya.custom_mobs.mobs.SpawnCustomMobListener;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobRegister;
import apple.voltskiya.custom_mobs.mobs.nms.parent.register.RegisteredCustomMob;
import apple.voltskiya.custom_mobs.pathfinders.PathfinderGoalBowShootNoBow;
import com.mojang.datafixers.types.Type;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.monster.EntitySkeleton;
import net.minecraft.world.entity.monster.EntitySkeletonAbstract;
import net.minecraft.world.level.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.logging.Level;

public class MobRevenant extends EntitySkeleton implements RegisteredCustomMob {
    public static final String REGISTERED_NAME = "revenant";
    private static EntityTypes<MobRevenant> entityTypes;
    private AttributeMapBase attributeMap = null;

    public static void initialize() {
        Map<? super Object, Type<?>> types = NmsMobRegister.getMinecraftTypes();
        final Type<?> oldType = types.get("minecraft:skeleton");
        types.put(registeredNameId(), oldType);

        // build it
        EntityTypes.Builder<MobRevenant> entitytypesBuilder = EntityTypes.Builder.a(MobRevenant::new, DecodeEnumCreatureType.MONSTER.encode());
        entityTypes = entitytypesBuilder.a(REGISTERED_NAME);
        entityTypes = IRegistry.a(DecodeIRegistry.getEntityType(), DecodeIRegistry.getEntityType().getId(DecodeEntityTypes.SKELETON), REGISTERED_NAME, entityTypes); // this is good

        // log it
        PluginNmsMobs.get().log(Level.INFO, "registered " + registeredNameId());
    }

    public MobRevenant(EntityTypes<? extends EntitySkeleton> entitytypes, World world) {
        super(DecodeEntityTypes.SKELETON, world);
    }

    @NotNull
    private static String registeredNameId() {
        return "minecraft" + ":" + REGISTERED_NAME;
    }

    @Override
    public EntityTypes<?> getEntityType() {
        return entityTypes;
    }

    @Override
    public AttributeMapBase getAttributeMap() {
        return this.attributeMap == null ? this.attributeMap = new AttributeMapBase(getAttributeProvider()) : this.attributeMap;
    }

    public AttributeProvider getAttributeProvider() {
        return EntitySkeletonAbstract.n()
                .a(DecodeGenericAttributes.FOLLOW_RANGE, 50)
                .a();
    }

    /**
     * spawns a WarpedGremlin
     *
     * @param location the org.bukkit location where the mob should be spawned
     * @param oldNbt   the nbt of the previously spawned mob or null if no entity existed
     */
    public static void spawn(Location location, NBTTagCompound oldNbt) {
        CraftWorld world = (CraftWorld) location.getWorld();
        final MobRevenant entity = new MobRevenant(entityTypes, world.getHandle());
        entity.prepare(location, oldNbt);
        entity.addScoreboardTag(SpawnCustomMobListener.CUSTOM_SPAWN_COMPLETE_TAG);
        entity.addScoreboardTag(REGISTERED_NAME);
        world.getHandle().addEntity(entity);
    }


    public static void spawnEat(CreatureSpawnEvent event) {
        Location location = event.getEntity().getLocation();
        spawn(location, ((CraftEntity) event.getEntity()).getHandle().save(new NBTTagCompound()));
        event.setCancelled(true);
    }

    private void prepare(Location location, NBTTagCompound oldNbt) {
        if (oldNbt != null) {
            oldNbt.remove("UUID");
            this.load(oldNbt);
        }
        this.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }


    @Override
    public void load(NBTTagCompound nbttagcompound) {
        nbttagcompound.setString("id", registeredNameId());
        super.load(nbttagcompound);
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        NBTTagCompound data = super.save(nbttagcompound);
        data.setString("id", registeredNameId());
        return data;
    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();
        DecodeEntity.getGoalSelector(this).a(4, new PathfinderGoalBowShootNoBow<>(this, 1.0D, 20, 15.0F));
    }

    @Override
    public void a(PacketPlayOutSpawnEntityLiving packetplayoutspawnentityliving) {
        super.a(packetplayoutspawnentityliving);
    }

    @Override
    public Packet<?> getPacket() {
        return super.getPacket();
    }

    @Override
    public void t() {
        // do no special pathfinding
    }
}
