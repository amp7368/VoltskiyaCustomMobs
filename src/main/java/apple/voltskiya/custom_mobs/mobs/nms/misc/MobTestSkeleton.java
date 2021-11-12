package apple.voltskiya.custom_mobs.mobs.nms.misc;

import apple.nms.decoding.entity.DecodeEnumCreatureType;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.mobs.PluginNmsMobs;
import apple.voltskiya.custom_mobs.mobs.SpawnCustomMobListener;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobRegister;
import apple.voltskiya.custom_mobs.mobs.nms.parent.register.RegisteredCustomMob;
import com.mojang.datafixers.types.constant.EmptyPartPassthrough;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.monster.EntitySkeleton;
import net.minecraft.world.level.World;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.attribute.CraftAttributeMap;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.logging.Level;

public class MobTestSkeleton extends EntitySkeleton implements RegisteredCustomMob {
    public static final String REGISTERED_NAME = "skele";
    private static final AttributeProvider attributeProvider = EntitySkeleton.n().a();
    private static final DataWatcherObject<? super Boolean> entityTypesId;
    private static EntityTypes<MobTestSkeleton> entityTypes;

    static {
        System.out.println(EntitySkeleton.c);
        entityTypesId = DataWatcher.a(MobTestSkeleton.class, DataWatcherRegistry.i);
        System.out.println(entityTypesId);
    }

    private AttributeMapBase attributeMap;

    /**
     * constructor to match the EntityTypes requirement
     *
     * @param world the world to spawn the entity in
     */

    public MobTestSkeleton(EntityTypes<MobTestSkeleton> entityTypes, World world) {
        super(EntityTypes.aB, world);
        CraftAttributeMap correctCraftAttributes = new CraftAttributeMap(this.getAttributeMap());
        for (Attribute attribute : Attribute.values()) {
            AttributeInstance correctAttribute = correctCraftAttributes.getAttribute(attribute);
            if (correctAttribute == null) continue;
            AttributeInstance badAttribute = this.craftAttributes.getAttribute(attribute);
//            if (badAttribute == null) continue;
            badAttribute.setBaseValue(correctAttribute.getBaseValue());
            for (AttributeModifier modifier : badAttribute.getModifiers()) {
                badAttribute.removeModifier(modifier);
            }
            for (AttributeModifier modifier : correctAttribute.getModifiers()) {
                badAttribute.addModifier(modifier);
            }
        }

        System.out.println("hmmmm");
        System.out.println(IRegistry.Y.getId(this.getEntityType()));
        System.out.println(getSaveID());
    }

    public static void initialize() {
        // register the  datafixer
        NmsMobRegister.getMinecraftTypes().put(registeredNameId(), new EmptyPartPassthrough());

        // build it
        EntityTypes.Builder<MobTestSkeleton> entitytypesBuilder = EntityTypes.Builder.a(MobTestSkeleton::new, DecodeEnumCreatureType.MONSTER.encode());
        entityTypes = IRegistry.a(IRegistry.Y, getRegisteredMinecraftKey(), entitytypesBuilder.a(registeredNameId()));
        System.out.println(entityTypes);
        // log it
        PluginNmsMobs.get().log(Level.INFO, "registered " + getRegisteredMinecraftKey());
    }

    @NotNull
    private static String registeredNameId() {
        return REGISTERED_NAME;
    }

    @NotNull
    private static MinecraftKey getRegisteredMinecraftKey() {
        return new MinecraftKey(getRegisteredNamespace().getNamespace(), getRegisteredNamespace().getKey());
    }

    @NotNull
    private static NamespacedKey getRegisteredNamespace() {
        return new NamespacedKey(VoltskiyaPlugin.get(), REGISTERED_NAME);
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
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        final MobTestSkeleton mob = new MobTestSkeleton(entityTypes, world);
        mob.prepare(location, oldNbt);
        mob.addScoreboardTag(SpawnCustomMobListener.CUSTOM_SPAWN_COMPLETE_TAG);
        mob.addScoreboardTag(REGISTERED_NAME);
        world.addEntity(mob);
    }

    @Override
    public EntityTypes<?> getEntityType() {
        return entityTypes;
    }

    public PacketPlayOutEntityMetadata getEntityMetadataPacket() {
        return new PacketPlayOutEntityMetadata(getId(), getDataWatcher(), true);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
    }

    private void prepare(Location location, NBTTagCompound oldNbt) {
        if (oldNbt != null)
            this.load(oldNbt);
        this.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    @Override
    public AttributeMapBase getAttributeMap() {
        return this.attributeMap == null ? this.attributeMap = new AttributeMapBase(attributeProvider) : this.attributeMap;
    }

    @Override
    public CraftEntity getBukkitEntity() {
        return super.getBukkitEntity();
    }


    @Override
    protected void initPathfinder() {
    }

}
