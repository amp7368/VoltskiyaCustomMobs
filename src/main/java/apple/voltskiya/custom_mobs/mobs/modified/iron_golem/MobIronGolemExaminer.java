package apple.voltskiya.custom_mobs.mobs.modified.iron_golem;

import apple.voltskiya.custom_mobs.mobs.PluginNmsMobs;
import apple.voltskiya.custom_mobs.mobs.SpawnCustomMobListener;
import apple.voltskiya.custom_mobs.mobs.pathfinders.PathfinderGoalHurtByTargetExcept;
import com.mojang.datafixers.types.Type;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.logging.Level;

public class MobIronGolemExaminer extends EntityIronGolem {
    public static final String REGISTERED_NAME = "mob.examiner.iron_golem";
    private static EntityTypes<MobIronGolemExaminer> entityTypes;

    public MobIronGolemExaminer(EntityTypes<? extends EntityIronGolem> entitytypes, World world) {
        super(EntityTypes.IRON_GOLEM, world);
    }

    /**
     * registers the IronGolemExaminer as an entity
     */
    public static void initialize() {
        Map<? super Object, Type<?>> types = PluginNmsMobs.getMinecraftTypes();
        final Type<?> oldType = types.get("minecraft:iron_golem");
        types.put(registeredNameId(), oldType);

        // build it
        EntityTypes.Builder<MobIronGolemExaminer> entitytypesBuilder = EntityTypes.Builder.a(MobIronGolemExaminer::new, EnumCreatureType.MONSTER);
        entityTypes = entitytypesBuilder.a(REGISTERED_NAME);
        entityTypes = IRegistry.a(IRegistry.ENTITY_TYPE, IRegistry.ENTITY_TYPE.a(EntityTypes.IRON_GOLEM), REGISTERED_NAME, entityTypes); // this is good

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
        final MobIronGolemExaminer gremlin = new MobIronGolemExaminer(entityTypes, world.getHandle());
        gremlin.prepare(location, oldNbt);
        gremlin.addScoreboardTag(SpawnCustomMobListener.CUSTOM_SPAWN_COMPLETE_TAG);
        gremlin.addScoreboardTag(REGISTERED_NAME);
        world.getHandle().addEntity(gremlin);
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
        // copied from super.initPathfinder()
        super.initPathfinder();
        this.targetSelector = new PathfinderGoalSelector(world.getMethodProfilerSupplier());
        this.targetSelector.a(1, new PathfinderGoalDefendVillage(this));
        // modified to not attack illagers
        this.targetSelector.a(2, new PathfinderGoalHurtByTargetExcept(this, (e) -> e.getMonsterType() != EnumMonsterType.ILLAGER));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, 10, true, false, this::a_));
        // modified to not attack illagers
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityInsentient.class, 5, false, false,
                (entityliving) -> entityliving instanceof IMonster && !(entityliving instanceof EntityCreeper) && entityliving.getMonsterType() != EnumMonsterType.ILLAGER)
        );
        this.targetSelector.a(4, new PathfinderGoalUniversalAngerReset<>(this, false));
    }
}
