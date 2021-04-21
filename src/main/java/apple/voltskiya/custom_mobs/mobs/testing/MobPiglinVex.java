package apple.voltskiya.custom_mobs.mobs.testing;

import apple.voltskiya.custom_mobs.mobs.NmsMobsPlugin;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.event.CraftEventFactory;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTargetEvent;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class MobPiglinVex extends EntityVex implements IRangedEntity, IEntityAngerable {
    public static final String REGISTERED_NAME = "piglin_vex";
    private static EntityTypes<MobPiglinVex> entityTypes;
    private AttributeMapBase attributeMap = null;
    private int angriness;
    private UUID angerTarget;
    private Map<EnumItemSlot, ItemStack> equipment = new HashMap<>();

    /**
     * constructor to match the EntityTypes requirement
     *
     * @param entitytypes my entity type. me. this is me.
     * @param world       the world to spawn the entity in
     */
    protected MobPiglinVex(EntityTypes<MobPiglinVex> entitytypes, World world) {
        super(entitytypes, world);
    }

    /**
     * registers the WarpedGremlin as an entity
     */
    public static void initialize() {
        EntityTypes.Builder<MobPiglinVex> entitytypesBuilder = EntityTypes.Builder.a(MobPiglinVex::new, EnumCreatureType.MONSTER);
        entitytypesBuilder.a(2f, 2f);
        // this version of minecraft (whatever it happens to be)
        final int keyForVersion = DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion());
        // the thing to register stuff I think?
        final DataFixer dataFixerToRegister = DataConverterRegistry.a();

        final Schema schemaForSomething = dataFixerToRegister.getSchema(keyForVersion);
        final TaggedChoice.TaggedChoiceType<?> choiceType = schemaForSomething.findChoiceType(DataConverterTypes.ENTITY_TREE);

        // copy the zombie type to the warped gremlin type\
        // todo understand this more
        Map<? super Object, Type<?>> types = (Map<? super Object, Type<?>>) choiceType.types();
        final Type<?> zombieType = types.get("minecraft:vex");
        types.put("minecraft:" + REGISTERED_NAME, zombieType);

        // build it
        entityTypes = entitytypesBuilder.a(REGISTERED_NAME);
        // log it
        NmsMobsPlugin.get().log(Level.INFO, "registered " + REGISTERED_NAME);
    }

    /**
     * spawns a WarpedGremlin
     *
     * @param name     the name of the mob?
     * @param world    the org.bukkit world where the mob should be spawned
     * @param location the org.bukkit location where the mob should be spawned
     */
    public static void spawn(String name, org.bukkit.World world, org.bukkit.Location location) {
        final MobPiglinVex gremlin = new MobPiglinVex(entityTypes, ((CraftWorld) world).getHandle());
        gremlin.prepare();
        gremlin.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        ((CraftWorld) world).getHandle().addEntity(gremlin);
    }

    public void prepare() {
//        this.getBehaviorController().a(MemoryModuleType.HUNTED_RECENTLY, true, (long) TIME_TO_FORGET);
        if (random.nextBoolean()) {
            final ItemStack itemStack = new ItemStack(Items.BOW);
            this.setSlot(EnumItemSlot.MAINHAND, itemStack);
        } else {
            final ItemStack itemStack = new ItemStack(Items.WOODEN_SWORD);
            this.setSlot(EnumItemSlot.MAINHAND, itemStack);
        }
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        // add a goal of shooting
        this.goalSelector.a(4, new PathfinderGoalCharge());
        // new PathfinderGoalBowShoot(shooter, ???, timeToShoot, ???)
        this.goalSelector.a(4,new PathfinderGoalBowShoot<>(this, 1d, 20, 15.0f));
        this.goalSelector.a(8, new PathfinderGoalStrafeAndShoot(this, 1d, 20, 15.0f));
        this.goalSelector.a(9, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 3.0F, 1.0F));
        this.goalSelector.a(10, new PathfinderGoalLookAtPlayer(this, EntityInsentient.class, 8.0F));
        this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, EntityRaider.class)).a(new Class[0]));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.BLOCK_HONEY_BLOCK_SLIDE;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_SPIDER_STEP;
    }

    @Override
    public void die(DamageSource damagesource) {
        super.die(damagesource);
    }

    /**
     * @return EnumMonsterType.ARTHROPOD || EnumMonsterType.ILLAGER || ...
     */
    @Override
    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEAD;
    }

    @Override
    public EntityTypes<?> getEntityType() {
        return EntityTypes.VEX;
    }


    @Override
    public CraftEntity getBukkitEntity() {
        return super.getBukkitEntity();
    }

    @Override
    public AttributeMapBase getAttributeMap() {
        if (this.attributeMap == null) this.attributeMap = new AttributeMapBase(getAttributeProvider());
        return this.attributeMap;
    }

    /**
     * @return the default attributeMap
     */
    private static AttributeProvider getAttributeProvider() {
        return EntityMonster.eR().a(GenericAttributes.MAX_HEALTH, 16.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.3499999940395355D).a(GenericAttributes.ATTACK_DAMAGE, 5.0D).a();
    }

    @Override
    public void movementTick() {
        super.movementTick();
    }

    @Override
    public void move(EnumMoveType enummovetype, Vec3D vec3d) {
        super.move(enummovetype, vec3d);
    }


    @Override
    public void setPose(EntityPose pose) {
        super.setPose(pose);
    }

    @Override
    public boolean isBaby() {
        return super.isBaby();
    }

    @Override
    public EnumMainHand getMainHand() {
        return EnumMainHand.RIGHT;
    }

    private boolean isHasBow() {
        return this.getItemInMainHand().getItem().equals(Items.BOW);
    }

    /**
     * shoot an arrow at the target
     *
     * @param target the target to shoot at
     * @param damage the damage of the arrow
     */
    @Override
    public void a(EntityLiving target, float damage) {
        System.out.println("shot!");
        final EnumHand handToShootWith = EnumHand.MAIN_HAND;
        ItemStack equipmentInHandChecked = /*this.f(*/this.b(handToShootWith);//);
        EntityArrow entityarrow = ProjectileHelper.a(this, equipmentInHandChecked, damage); // v is damage
        double d0 = target.locX() - this.locX();
        double d1 = target.e(1 / 3d) - entityarrow.locY();
        double d2 = target.locZ() - this.locZ();
        double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
        entityarrow.shoot(d0, d1 + d3 * 0.2D, d2, 1.6F, (float) (14 - this.world.getDifficulty().a() * 4));
        EntityShootBowEvent event = CraftEventFactory.callEntityShootBowEvent(this, equipmentInHandChecked, null, entityarrow, handToShootWith, 0.8F, false);
        if (event.isCancelled()) {
            event.getProjectile().remove();
        } else {
            if (event.getProjectile() == entityarrow.getBukkitEntity()) {
                this.world.addEntity(entityarrow);
            }
            this.playSound(SoundEffects.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        }
    }

    @Override
    public int getAnger() {
        return this.angriness;
    }

    @Override
    public void setAnger(int angriness) {
        this.angriness = angriness;
    }

    @Override
    @Nullable
    public UUID getAngerTarget() {
        return this.angerTarget;
    }

    @Override
    public void setAngerTarget(@Nullable UUID angerTarget) {
        this.angerTarget = angerTarget;
    }

    @Override
    public void anger() {
        double angerDistance = this.b(GenericAttributes.FOLLOW_RANGE);
        AxisAlignedBB angerBox = AxisAlignedBB.a(this.getPositionVector()).grow(angerDistance, 10.0D, angerDistance);
        // get all the entities in this box, and angryify them
        this.world.b(this.getClass(), angerBox).stream().filter((entityInBox) ->
                entityInBox != this && entityInBox.getGoalTarget() == null && !entityInBox.r(this.getGoalTarget())
        ).forEach((angryMob) -> {
            angryMob.setGoalTarget(this.getGoalTarget(), EntityTargetEvent.TargetReason.TARGET_ATTACKED_NEARBY_ENTITY, true);
        });
    }

    private class PathfinderGoalStrafeAndShoot extends PathfinderGoal{//extends PathfinderGoalBowShoot<MobPiglinVex> {
        public PathfinderGoalStrafeAndShoot(MobPiglinVex var0, double var1, int var3, float var4) {
//            super(var0, var1, var3, var4);
        }

        @Override
        public boolean a() {
            return !MobPiglinVex.this.getControllerMove().b() && MobPiglinVex.this.random.nextInt(7) == 0;
        }

        public boolean b() {
            return false;
        }

        /**
         * do the move
         */
        @Override
        public void e() {
            BlockPosition blockposition = MobPiglinVex.this.eL();
            if (blockposition == null) {
                blockposition = MobPiglinVex.this.getChunkCoordinates();
            }
            for (int i = 0; i < 3; ++i) {
                BlockPosition blockposition1 = blockposition.b(MobPiglinVex.this.random.nextInt(15) - 7, MobPiglinVex.this.random.nextInt(11) - 5, MobPiglinVex.this.random.nextInt(15) - 7);
                if (MobPiglinVex.this.world.isEmpty(blockposition1)) {
                    MobPiglinVex.this.moveController.a((double) blockposition1.getX() + 0.5D, (double) blockposition1.getY() + 0.5D, (double) blockposition1.getZ() + 0.5D, 0.25D);
                    if (MobPiglinVex.this.getGoalTarget() == null) {
                        MobPiglinVex.this.getControllerLook().a((double) blockposition1.getX() + 0.5D, (double) blockposition1.getY() + 0.5D, (double) blockposition1.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }

            super.e();
        }
    }

    private class PathfinderGoalCharge extends PathfinderGoal {
        public PathfinderGoalCharge() {
            this.a(EnumSet.of(Type.MOVE));
        }

        public boolean a() {
            return !MobPiglinVex.this.isHasBow() && !MobPiglinVex.this.getControllerMove().b() && MobPiglinVex.this.getGoalTarget() != null && MobPiglinVex.this.random.nextInt(7) == 0 && MobPiglinVex.this.h(MobPiglinVex.this.getGoalTarget()) > 4.0D;
        }

        public boolean b() {
            return MobPiglinVex.this.getControllerMove().b() && MobPiglinVex.this.isCharging() && MobPiglinVex.this.getGoalTarget() != null && MobPiglinVex.this.getGoalTarget().isAlive();
        }

        public void c() {
            EntityLiving entityliving = MobPiglinVex.this.getGoalTarget();
            if (entityliving != null) {
                Vec3D vec3d = entityliving.j(1.0F);
                MobPiglinVex.this.moveController.a(vec3d.x, vec3d.y, vec3d.z, 1.0D);
                MobPiglinVex.this.setCharging(true);
                MobPiglinVex.this.playSound(SoundEffects.ENTITY_VEX_CHARGE, 1.0F, 1.0F);
            }
        }

        public void d() {
            MobPiglinVex.this.setCharging(false);
        }

        public void e() {
            EntityLiving entityliving = MobPiglinVex.this.getGoalTarget();
            if (entityliving != null) {
                if (MobPiglinVex.this.getBoundingBox().c(entityliving.getBoundingBox())) {
                    MobPiglinVex.this.attackEntity(entityliving);
                    MobPiglinVex.this.setCharging(false);
                } else {
                    double d0 = MobPiglinVex.this.h(entityliving);
                    if (d0 < 9.0D) {
                        Vec3D vec3d = entityliving.j(1.0F);
                        MobPiglinVex.this.moveController.a(vec3d.x, vec3d.y, vec3d.z, 1.0D);
                    }
                }
            }
        }

    }
}