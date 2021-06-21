package apple.voltskiya.custom_mobs.mobs.modified.illager.pillager;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.entity.DecodeEnumCreatureType;
import apple.nms.decoding.iregistry.DecodeEntityTypes;
import apple.nms.decoding.iregistry.DecodeIRegistry;
import apple.voltskiya.custom_mobs.mobs.PluginNmsMobs;
import apple.voltskiya.custom_mobs.mobs.RegisteredCustomMob;
import apple.voltskiya.custom_mobs.mobs.SpawnCustomMobListener;
import com.mojang.datafixers.types.Type;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSelector;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.animal.EntityIronGolem;
import net.minecraft.world.entity.monster.EntityIllagerAbstract;
import net.minecraft.world.entity.monster.EntityPillager;
import net.minecraft.world.entity.npc.EntityVillagerAbstract;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.raid.EntityRaider;
import net.minecraft.world.level.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.logging.Level;

public class MobIllagerPillagerExaminer extends EntityPillager implements RegisteredCustomMob {
    public static final String REGISTERED_NAME = "mob.examiner.pillager";
    private static EntityTypes<MobIllagerPillagerExaminer> entityTypes;

    public static void initialize() {
        Map<? super Object, Type<?>> types = PluginNmsMobs.getMinecraftTypes();
        final Type<?> oldType = types.get("minecraft:pillager");
        types.put(registeredNameId(), oldType);

        // build it
        EntityTypes.Builder<MobIllagerPillagerExaminer> entitytypesBuilder = EntityTypes.Builder.a(MobIllagerPillagerExaminer::new, DecodeEnumCreatureType.MONSTER.encode());
        entityTypes = entitytypesBuilder.a(REGISTERED_NAME);
        entityTypes = IRegistry.a(DecodeIRegistry.getEntityType(), DecodeIRegistry.getEntityType().getId(DecodeEntityTypes.PILLAGER), REGISTERED_NAME, entityTypes); // this is good

        // log it
        PluginNmsMobs.get().log(Level.INFO, "registered " + registeredNameId());
    }


    public MobIllagerPillagerExaminer(EntityTypes<? extends EntityIllagerAbstract> entitytypes, World world) {
        super(DecodeEntityTypes.PILLAGER, world);
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
        final MobIllagerPillagerExaminer entity = new MobIllagerPillagerExaminer(entityTypes, world.getHandle());
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
        DecodeEntity.setTargetSelector(this, new PathfinderGoalSelector(getWorld().getMethodProfilerSupplier()));
        PathfinderGoalSelector targetSelector = DecodeEntity.getTargetSelector(this);
        targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, EntityIronGolem.class, EntityRaider.class)).a(new Class[0]));
        targetSelector.a(2, (new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true)).a(300));
        targetSelector.a(3, (new PathfinderGoalNearestAttackableTarget<>(this, EntityVillagerAbstract.class, false)).a(300));
    }
}
