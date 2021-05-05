package apple.voltskiya.custom_mobs.mobs.modified.illager.illusioner;

import apple.voltskiya.custom_mobs.mobs.PluginNmsMobs;
import apple.voltskiya.custom_mobs.mobs.SpawnCustomMobListener;
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

public class MobIllagerIllusionerExaminer extends EntityIllagerIllusioner {
    public static final String REGISTERED_NAME = "mob.examiner.illusioner";
    private static EntityTypes<MobIllagerIllusionerExaminer> entityTypes;

    public MobIllagerIllusionerExaminer(EntityTypes<? extends EntityIllagerAbstract> entitytypes, World world) {
        super(EntityTypes.ILLUSIONER, world);
    }

    /**
     * registers the IllagerExaminer as an entity
     */
    public static void initialize() {
        EntityTypes.Builder<MobIllagerIllusionerExaminer> entitytypesBuilder = EntityTypes.Builder.a(MobIllagerIllusionerExaminer::new, EnumCreatureType.MONSTER);

        // this version of minecraft (whatever it happens to be)
        final int keyForVersion = DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion());
        // the thing to register stuff I think?
        final DataFixer dataFixerToRegister = DataConverterRegistry.a();

        final Schema schemaForSomething = dataFixerToRegister.getSchema(keyForVersion);
        final TaggedChoice.TaggedChoiceType<?> choiceType = schemaForSomething.findChoiceType(DataConverterTypes.ENTITY_TREE);

        // copy the zombie type to the warped gremlin type
        // todo understand this more
        Map<? super Object, Type<?>> types = (Map<? super Object, Type<?>>) choiceType.types();
        final Type<?> oldType = types.get("minecraft:illusioner");
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
        final MobIllagerIllusionerExaminer examiner = new MobIllagerIllusionerExaminer(entityTypes, world.getHandle());
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
        super.initPathfinder();
        this.targetSelector = new PathfinderGoalSelector(world.getMethodProfilerSupplier());
        this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, EntityIronGolem.class, EntityRaider.class)).a(new Class[0]));
        this.targetSelector.a(2, (new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true)).a(300));
        this.targetSelector.a(3, (new PathfinderGoalNearestAttackableTarget<>(this, EntityVillagerAbstract.class, false)).a(300));
    }
}
