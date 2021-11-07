package apple.voltskiya.custom_mobs.mobs.nms.overworld.apc33;

import apple.nms.decoding.attribute.DecodeGenericAttributes;
import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.entity.DecodeEnumCreatureType;
import apple.nms.decoding.iregistry.DecodeEntityTypes;
import apple.nms.decoding.iregistry.DecodeIRegistry;
import apple.voltskiya.custom_mobs.mobs.PluginNmsMobs;
import apple.voltskiya.custom_mobs.mobs.RegisteredCustomMob;
import apple.voltskiya.custom_mobs.mobs.SpawnCustomMobListener;
import apple.voltskiya.custom_mobs.mobs.nms.parts.MobPartChild;
import apple.voltskiya.custom_mobs.mobs.nms.parts.MobPartMother;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModelConfig;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModelEntityConfig;
import apple.voltskiya.custom_mobs.mobs.nms.utils.UtilsPacket;
import apple.voltskiya.custom_mobs.pathfinders.spell.PathfinderGoalShootSpell;
import com.mojang.datafixers.types.Type;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSelector;
import net.minecraft.world.entity.monster.EntityRavager;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class MobAPC33 extends EntityRavager implements RegisteredCustomMob {
    private static final NmsModelConfig.ModelConfigName REGISTERED_MODEL = NmsModelConfig.ModelConfigName.APC33;
    public static final String REGISTERED_NAME = REGISTERED_MODEL.getFile();
    private static EntityTypes<MobAPC33> entityTypes;
    private static NmsModelEntityConfig selfModel;
    private AttributeMapBase attributeMap = null;
    private List<MobPartChild> children;

    public MobAPC33(EntityTypes<? extends EntityRavager> entitytypes, World world) {
        super(DecodeEntityTypes.RAVAGER, world);
    }

    public static void initialize() {
        Map<? super Object, Type<?>> types = PluginNmsMobs.getMinecraftTypes();
        final Type<?> oldType = types.get("minecraft:ravager");
        types.put(registeredNameId(), oldType);

        // build it
        EntityTypes.Builder<MobAPC33> entitytypesBuilder = EntityTypes.Builder.a(MobAPC33::new, DecodeEnumCreatureType.MONSTER.encode());
        entityTypes = entitytypesBuilder.a(REGISTERED_NAME);
        entityTypes = IRegistry.a(DecodeIRegistry.getEntityType(), DecodeIRegistry.getEntityType().getId(DecodeEntityTypes.RAVAGER), REGISTERED_NAME, entityTypes); // this is good

        // log it
        PluginNmsMobs.get().log(Level.INFO, "registered " + registeredNameId());
        final NmsModelConfig model = NmsModelConfig.parts(REGISTERED_MODEL);

        selfModel = model.mainPart();
    }

    @NotNull
    private static String registeredNameId() {
        return "minecraft" + ":" + REGISTERED_NAME;
    }

    /**
     * spawns a WarpedGremlin
     *
     * @param location the org.bukkit location where the mob should be spawned
     * @param oldNbt   the nbt of the previously spawned mob or null if no entity existed
     */
    public static void spawn(Location location, NBTTagCompound oldNbt) {
        CraftWorld world = (CraftWorld) location.getWorld();
        final MobAPC33 entity = new MobAPC33(entityTypes, world.getHandle());
        entity.prepare(location, oldNbt);
        entity.addChildren();
        entity.addScoreboardTag(SpawnCustomMobListener.CUSTOM_SPAWN_COMPLETE_TAG);
        entity.addScoreboardTag(REGISTERED_NAME);
        world.getHandle().addEntity(entity);
    }

    public static void spawnEat(CreatureSpawnEvent event) {
        Location location = event.getEntity().getLocation();
        spawn(location, ((CraftEntity) event.getEntity()).getHandle().save(new NBTTagCompound()));
        event.setCancelled(true);
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
        return EntityRavager.n()
                .a(DecodeGenericAttributes.FOLLOW_RANGE, 50)
                .a();
    }

    private void prepare(Location location, NBTTagCompound oldNbt) {
        if (oldNbt != null) {
            oldNbt.remove("UUID");
            this.load(oldNbt);
        }
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

    /**
     * move and move children
     *
     * @param enummovetype gift to super
     * @param vec3d        gift to super
     */
    @Override
    public void move(EnumMoveType enummovetype, Vec3D vec3d) {
        super.move(enummovetype, vec3d);
        List<Packet<?>> packetsToSend = new ArrayList<>();
        if (this.children == null) {
            this.addChildren();
        }
        for (MobPartChild child : children) {
            packetsToSend.add(child.moveFromMother(true));
        }
        UtilsPacket.sendPacketsToNearbyPlayers(packetsToSend, this.getBukkitEntity().getLocation());
    }

    /**
     * change worlds
     */
    @Override
    public @Nullable
    Entity b(WorldServer worldserver) {
        final Entity result = super.b(worldserver);
        if (result instanceof MobAPC33) {
            for (MobPartChild child : children) {
                child.die();
            }
            ((MobAPC33) result).addChildren();
        }
        return result;
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
        PathfinderGoalSelector goalSelector = DecodeEntity.getGoalSelector(this);
        goalSelector.a(new PathfinderGoalShootSpell<>(new MobAPC33SpellCaster(this), MobAPC33Config.MobAPCMachineGunType.NORMAL));
    }
}

