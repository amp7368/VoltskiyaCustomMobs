package apple.voltskiya.custom_mobs.mobs.modified.iron_golem;

import apple.voltskiya.custom_mobs.mobs.PluginNmsMobs;
import apple.voltskiya.custom_mobs.mobs.SpawnCustomMobListener;
import apple.voltskiya.custom_mobs.mobs.pathfinders.PathfinderGoalHurtByTargetExcept;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;

import javax.annotation.Nullable;
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
        EntityTypes.Builder<MobIronGolemExaminer> entitytypesBuilder = EntityTypes.Builder.a(MobIronGolemExaminer::new, EnumCreatureType.MONSTER);

        // this version of minecraft (whatever it happens to be)
        final int keyForVersion = DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion());
        // the thing to register stuff I think?
        final DataFixer dataFixerToRegister = DataConverterRegistry.a();

        final Schema schemaForSomething = dataFixerToRegister.getSchema(keyForVersion);
        final TaggedChoice.TaggedChoiceType<?> choiceType = schemaForSomething.findChoiceType(DataConverterTypes.ENTITY_TREE);

        // copy the zombie type to the warped gremlin type
        // todo understand this more
        Map<? super Object, Type<?>> types = (Map<? super Object, Type<?>>) choiceType.types();
        final Type<?> oldType = types.get("minecraft:iron_golem");
        types.put("minecraft:" + REGISTERED_NAME, oldType);

        // build it
        entityTypes = entitytypesBuilder.a(REGISTERED_NAME);

        // log it
        PluginNmsMobs.get().log(Level.INFO, "registered " + REGISTERED_NAME);
    }

    public static void spawnEat(CreatureSpawnEvent event) {
        final LivingEntity entity = event.getEntity();
        Location location = entity.getLocation();
        spawn(location, ((CraftEntity) entity).getHandle().save(new NBTTagCompound()));
        event.setCancelled(true);
    }

    private static void spawn(Location location, @Nullable NBTTagCompound oldNbt) {
        CraftWorld world = (CraftWorld) location.getWorld();
        final MobIronGolemExaminer examiner = new MobIronGolemExaminer(entityTypes, world.getHandle());
        examiner.prepare(location, oldNbt);
        examiner.addScoreboardTag(SpawnCustomMobListener.CUSTOM_SPAWN_COMPLETE_TAG);
        examiner.addScoreboardTag(REGISTERED_NAME);
        world.getHandle().addEntity(examiner);
    }

    private void prepare(Location location, NBTTagCompound oldNbt) {
        this.loadData(oldNbt);
        this.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
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
