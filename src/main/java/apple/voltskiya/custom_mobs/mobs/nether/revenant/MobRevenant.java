package apple.voltskiya.custom_mobs.mobs.nether.revenant;

import apple.voltskiya.custom_mobs.mobs.PluginNmsMobs;
import apple.voltskiya.custom_mobs.mobs.SpawnCustomMobListener;
import apple.voltskiya.custom_mobs.mobs.pathfinders.PathfinderGoalBowShootNoBow;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Map;
import java.util.logging.Level;

public class MobRevenant extends EntitySkeleton {
    public static final String REGISTERED_NAME = "revenant";
    private static EntityTypes<MobRevenant> entityTypes;

    /**
     * constructor to match the EntityTypes requirement
     *
     * @param world the world to spawn the entity in
     */

    public MobRevenant(EntityTypes<MobRevenant> entityTypes, World world) {
        super(EntityTypes.SKELETON, world);
    }

    /**
     * registers the WarpedGremlin as an entity
     */
    public static void initialize() {
        EntityTypes.Builder<MobRevenant> entitytypesBuilder = EntityTypes.Builder.a(MobRevenant::new, EnumCreatureType.MONSTER);

        // this version of minecraft (whatever it happens to be)
        final int keyForVersion = DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion());
        // the thing to register stuff I think?
        final DataFixer dataFixerToRegister = DataConverterRegistry.a();

        final Schema schemaForSomething = dataFixerToRegister.getSchema(keyForVersion);
        final TaggedChoice.TaggedChoiceType<?> choiceType = schemaForSomething.findChoiceType(DataConverterTypes.ENTITY_TREE);

        // copy the zombie type to this type
        Map<? super Object, Type<?>> types = (Map<? super Object, Type<?>>) choiceType.types();
        final Type<?> zombieType = types.get("minecraft:skeleton");
        types.put("minecraft:" + REGISTERED_NAME, zombieType);

        // build it
        entityTypes = entitytypesBuilder.a(REGISTERED_NAME);

        // log it
        PluginNmsMobs.get().log(Level.INFO, "registered " + REGISTERED_NAME);
    }

    /**
     * spawns a WarpedGremlin
     *
     * @param location the org.bukkit location where the mob should be spawned
     * @param oldNbt   the nbt of the previously spawned mob or null if no entity existed
     */
    public static void spawn(Location location, NBTTagCompound oldNbt) {
        CraftWorld world = (CraftWorld) location.getWorld();
        final MobRevenant revenant = new MobRevenant(entityTypes, world.getHandle());
        revenant.load(oldNbt);
        revenant.setLocation(location.getX(),location.getY(),location.getZ(),location.getYaw(),location.getPitch());
        revenant.addScoreboardTag(SpawnCustomMobListener.CUSTOM_SPAWN_COMPLETE_TAG);
        world.getHandle().addEntity(revenant);
    }

    public static void spawnEat(CreatureSpawnEvent event) {
        Location location = event.getEntity().getLocation();
        spawn(location, ((CraftEntity) event.getEntity()).getHandle().save(new NBTTagCompound()));
        event.setCancelled(true);
    }

    @Override
    protected void initPathfinder() {
        final AttributeModifiable followRange = this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE);
        if (followRange != null) followRange.setValue(50);
        this.goalSelector.a(2, new PathfinderGoalRestrictSun(this));
        this.goalSelector.a(3, new PathfinderGoalFleeSun(this, 1.0D));
        this.goalSelector.a(3, new PathfinderGoalAvoidTarget<>(this, EntityWolf.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.a(4,new PathfinderGoalBowShootNoBow<>(this, 1.0D, 20, 15.0F));
        this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(6, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, true));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityTurtle.class, 10, true, false, EntityTurtle.bo));
    }

    @Override
    public void eL() {
//        if (this.world != null && !this.world.isClientSide) {
//            this.goalSelector.a(this.b);
//            ItemStack itemstack = this.b(ProjectileHelper.a(this, Items.BOW));
//            if (itemstack.getItem() == Items.BOW) {
//                byte b0 = 20;
//                if (this.world.getDifficulty() != EnumDifficulty.HARD) {
//                    b0 = 40;
//                }
//
//                this.b.a(b0);
//                this.goalSelector.a(4, this.b);
//            } else {
//                this.goalSelector.a(4, this.c);
//            }
//        }

    }
}
