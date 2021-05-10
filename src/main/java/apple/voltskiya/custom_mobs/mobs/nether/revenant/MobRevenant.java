package apple.voltskiya.custom_mobs.mobs.nether.revenant;

import apple.voltskiya.custom_mobs.mobs.PluginNmsMobs;
import apple.voltskiya.custom_mobs.mobs.RegisteredCustomMob;
import apple.voltskiya.custom_mobs.mobs.SpawnCustomMobListener;
import apple.voltskiya.custom_mobs.pathfinders.PathfinderGoalBowShootNoBow;
import com.mojang.datafixers.types.Type;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.logging.Level;

public class MobRevenant extends EntitySkeleton implements RegisteredCustomMob {
    public static final String REGISTERED_NAME = "revenant";
    private static EntityTypes<MobRevenant> entityTypes;
    private AttributeMapBase attributeMap = null;

    public MobRevenant(EntityTypes<? extends EntitySkeleton> entitytypes, World world) {
        super(EntityTypes.SKELETON, world);
    }

    public static void initialize() {
        Map<? super Object, Type<?>> types = PluginNmsMobs.getMinecraftTypes();
        final Type<?> oldType = types.get("minecraft:skeleton");
        types.put(registeredNameId(), oldType);

        // build it
        EntityTypes.Builder<MobRevenant> entitytypesBuilder = EntityTypes.Builder.a(MobRevenant::new, EnumCreatureType.MONSTER);
        entityTypes = entitytypesBuilder.a(REGISTERED_NAME);
        entityTypes = IRegistry.a(IRegistry.ENTITY_TYPE, IRegistry.ENTITY_TYPE.a(EntityTypes.SKELETON), REGISTERED_NAME, entityTypes); // this is good

        // log it
        PluginNmsMobs.get().log(Level.INFO, "registered " + registeredNameId());
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
        return EntitySkeleton.eR()
                .a(GenericAttributes.FOLLOW_RANGE, 50)
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
    public void eL() {
        // do no special pathfinding
    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();
        this.goalSelector.a(4, new PathfinderGoalBowShootNoBow<>(this, 1.0D, 20, 15.0F));
    }
}
