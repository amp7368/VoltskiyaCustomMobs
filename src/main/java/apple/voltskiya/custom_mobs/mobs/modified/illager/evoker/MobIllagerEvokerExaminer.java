package apple.voltskiya.custom_mobs.mobs.modified.illager.evoker;

import apple.voltskiya.custom_mobs.mobs.PluginNmsMobs;
import apple.voltskiya.custom_mobs.mobs.SpawnCustomMobListener;
import com.mojang.datafixers.types.Type;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.logging.Level;

public class MobIllagerEvokerExaminer extends EntityEvoker {
    public static final String REGISTERED_NAME = "mob.examiner.evoker";
    private static EntityTypes<MobIllagerEvokerExaminer> entityTypes;

    public MobIllagerEvokerExaminer(EntityTypes<? extends EntityEvoker> entitytypes, World world) {
        super(EntityTypes.EVOKER, world);
    }

    /**
     * registers the IllagerExaminer as an entity
     */
    public static void initialize() {
        Map<? super Object, Type<?>> types = PluginNmsMobs.getMinecraftTypes();
        final Type<?> oldType = types.get("minecraft:evoker");
        types.put(registeredNameId(), oldType);

        // build it
        EntityTypes.Builder<MobIllagerEvokerExaminer> entitytypesBuilder = EntityTypes.Builder.a(MobIllagerEvokerExaminer::new, EnumCreatureType.MONSTER);
        entityTypes = entitytypesBuilder.a(REGISTERED_NAME);
        entityTypes = IRegistry.a(IRegistry.ENTITY_TYPE, IRegistry.ENTITY_TYPE.a(EntityTypes.EVOKER), REGISTERED_NAME, entityTypes); // this is good

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

    /**
     * spawns a WarpedGremlin
     *
     * @param location the org.bukkit location where the mob should be spawned
     * @param oldNbt   the nbt of the previously spawned mob or null if no entity existed
     */
    public static void spawn(Location location, NBTTagCompound oldNbt) {
        CraftWorld world = (CraftWorld) location.getWorld();
        final MobIllagerEvokerExaminer entity = new MobIllagerEvokerExaminer(entityTypes, world.getHandle());
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
        if (oldNbt != null){
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
        this.targetSelector = new PathfinderGoalSelector(world.getMethodProfilerSupplier());
        this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, EntityIronGolem.class, EntityRaider.class)).a(new Class[0]));
        this.targetSelector.a(2, (new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true)).a(300));
        this.targetSelector.a(3, (new PathfinderGoalNearestAttackableTarget<>(this, EntityVillagerAbstract.class, false)).a(300));
    }
}
