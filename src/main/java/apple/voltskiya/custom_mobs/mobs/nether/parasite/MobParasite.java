package apple.voltskiya.custom_mobs.mobs.nether.parasite;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.custom_model.CustomModel;
import apple.voltskiya.custom_mobs.mobs.NmsMobsPlugin;
import apple.voltskiya.custom_mobs.mobs.SpawnCustomMobListener;
import apple.voltskiya.custom_mobs.mobs.parts.*;
import apple.voltskiya.custom_mobs.mobs.parts.NmsModelConfig.ModelConfigName;
import apple.voltskiya.custom_mobs.mobs.pathfinders.PathfinderGoalCraveBlock;
import apple.voltskiya.custom_mobs.mobs.utils.UtilsAttribute;
import apple.voltskiya.custom_mobs.mobs.utils.UtilsPacket;
import apple.voltskiya.custom_mobs.util.EntityLocation;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftZombie;
import org.bukkit.event.entity.CreatureSpawnEvent;

import javax.annotation.Nullable;
import java.util.*;
import java.util.logging.Level;

public class MobParasite extends EntityZombie {
    public static final ModelConfigName REGISTERED_MODEL = ModelConfigName.PARASITE;
    public static final String REGISTERED_NAME = REGISTERED_MODEL.getFile();
    private static EntityTypes<MobParasite> entityTypes;
    private final List<MobPartChild> children = new ArrayList<>();
    private final NmsModelEntityConfig selfModel;
    private boolean isWingHigh = true;

    /**
     * constructor to match the EntityTypes requirement
     *
     * @param world the world to spawn the entity in
     */

    public MobParasite(EntityTypes<MobParasite> entityTypes, World world) {
        super(EntityTypes.ZOMBIE, world);
        UtilsAttribute.fillAttributes(this.getAttributeMap(), getAttributeProvider());
        this.selfModel = NmsModelConfig.parts(REGISTERED_MODEL).mainPart();
    }

    /**
     * registers the NetherParasite as an entity
     */
    public static void initialize() {
        EntityTypes.Builder<MobParasite> entitytypesBuilder = EntityTypes.Builder.a(MobParasite::new, EnumCreatureType.MONSTER);

        // this version of minecraft (whatever it happens to be)
        final int keyForVersion = DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion());
        // the thing to register stuff I think?
        final DataFixer dataFixerToRegister = DataConverterRegistry.a();

        final Schema schemaForSomething = dataFixerToRegister.getSchema(keyForVersion);
        final TaggedChoice.TaggedChoiceType<?> choiceType = schemaForSomething.findChoiceType(DataConverterTypes.ENTITY_TREE);

        // copy the zombie type to the warped gremlin type
        Map<? super Object, Type<?>> types = (Map<? super Object, Type<?>>) choiceType.types();
        final Type<?> oldType = types.get("minecraft:cave_spider");
        types.put("minecraft:" + REGISTERED_NAME, oldType);

        // build it
        entityTypes = entitytypesBuilder.a(REGISTERED_NAME);

        // log it
        NmsMobsPlugin.get().log(Level.INFO, "registered " + REGISTERED_NAME);
    }

    /**
     * spawns a WarpedGremlin
     *
     * @param location the org.bukkit location where the mob should be spawned
     * @param oldNbt   the nbt of the previously spawned mob or null if no entity existed
     */
    public static void spawn(Location location, NBTTagCompound oldNbt) {
        CraftWorld world = (CraftWorld) location.getWorld();
        final MobParasite parasite = new MobParasite(entityTypes, world.getHandle());
        parasite.prepareChildren(location, oldNbt);
        parasite.addScoreboardTag(SpawnCustomMobListener.CUSTOM_SPAWN_COMPLETE_TAG);
        parasite.addScoreboardTag(REGISTERED_NAME);
        world.getHandle().addEntity(parasite);
    }

    public static void spawnEat(CreatureSpawnEvent event) {
        Location location = event.getEntity().getLocation();
        spawn(location, ((CraftEntity) event.getEntity()).getHandle().save(new NBTTagCompound()));
        event.setCancelled(true);
    }

    public static void spawn(Location location, NBTTagCompound oldNbt, Vec3D velocity) {
        CraftWorld world = (CraftWorld) location.getWorld();
        final MobParasite parasite = new MobParasite(entityTypes, world.getHandle());
        parasite.prepareChildren(location, oldNbt);
        parasite.addScoreboardTag(SpawnCustomMobListener.CUSTOM_SPAWN_COMPLETE_TAG);
        parasite.addScoreboardTag(REGISTERED_NAME);
        parasite.setMot(velocity);
        world.getHandle().addEntity(parasite);
    }

    private void prepareChildren(Location location, NBTTagCompound oldNbt) {
        final NmsModelConfig model = NmsModelConfig.parts(REGISTERED_MODEL);
        final NBTTagCompound newNbt = this.selfModel.getEntity().nbt;
        final NBTTagCompound mergedNbt = oldNbt == null ? newNbt : oldNbt.a(newNbt);
        this.loadData(mergedNbt);
        this.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        EntityLocation motherLocation = new EntityLocation(
                this.getUniqueID(),
                this.selfModel.getEntity().x,
                this.selfModel.getEntity().y,
                this.selfModel.getEntity().z,
                this.selfModel.getEntity().facingX,
                this.selfModel.getEntity().facingY,
                this.selfModel.getEntity().facingZ
        ); // for simpler rotations
        MobPartMother motherMe = new MobPartMother(motherLocation, this, REGISTERED_NAME);
        for (NmsModelEntityConfig part : model.others()) {
            children.add(MobParts.spawnMobPart(motherMe, part));
        }
    }

    @Override
    public void tick() {
        super.tick();
        wingFlap();
    }

    private void wingFlap() {

    }

    @Override
    protected void initPathfinder() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
            // if this is the overworld, do the overworld AI, otherwise, get to the overworld
            if (DimensionManager.OVERWORLD.a().equals((this.world.getDimensionKey().a()))) {
                this.initPathfinderOverworld();
            } else {
                this.initPathfinderOtherWorld();
            }
        }, 5 * 10);
    }

//    @Override
//    protected NavigationAbstract b(World world) {
//        return new Nav(this, world);
//    }


    private void initPathfinderOtherWorld() {
        // new PathfinderGoalRandomStrollLand(entityToMove, speed, chanceToActivate)
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalCraveBlock(this, Collections.singletonList(org.bukkit.Material.NETHER_PORTAL), 512, 20, 2, 1.0));
        this.goalSelector.a(2, new PathfinderGoalRandomStrollLand(this, 1.0D, 0.003F));
    }

    private void initPathfinderOverworld() {
        final int chanceToCheck = 20;
        int chanceToCheckPlayer = 60;
        this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityAnimal.class, chanceToCheck, true, false, (entityLiving) -> !entityLiving.getScoreboardTags().contains(MobInfected.PARASITE_INFECTED_TAG)));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityPlayer.class, chanceToCheckPlayer, true, false, null));
        double speed = 1.2;
        this.goalSelector.a(1, new PathfinderGoalMeleeAttack(this, speed, true));
        this.goalSelector.a(2, new PathfinderGoalRandomStrollLand(this, 1.0D, 0.003F));
    }

    @Override
    public boolean attackEntity(Entity e) {
        if (e instanceof EntityPlayer) return super.attackEntity(e);
        if (e instanceof EntityCreature) {
            if (!e.getScoreboardTags().contains(MobInfected.PARASITE_INFECTED_TAG)) {
                EntityCreature entity = (EntityCreature) e;
                new MobInfected(entity);
                die();
            }
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> this.setGoalTarget(null), 40);
        return false;
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        final boolean invisible = nbttagcompound.getBoolean("Invisible");
        ((CraftZombie) getBukkitEntity()).setInvisible(invisible);
        this.setInvisible(invisible);
    }

    /**
     * @return EnumMonsterType.ARTHROPOD || EnumMonsterType.ILLAGER || ...
     */
    @Override
    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.ARTHROPOD;
    }

    @Override
    public EntityTypes<?> getEntityType() {
        return EntityTypes.ZOMBIE;
    }

    @Override
    public void die() {
        super.die();
        for (MobPartChild child : children) {
            child.die();
        }
    }

    @Override
    public void setOnFire(int i) {

    }

    /**
     * @return the default attributeMap
     */
    private static AttributeProvider getAttributeProvider() {
        return EntityZombie.eR().a();
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
        for (MobPartChild child : children) {
            packetsToSend.add(child.moveFromMother(false));
        }
        UtilsPacket.sendPacketsToAllPlayers(packetsToSend);
    }


    /**
     * change worlds and move children
     */
    @Override
    public @Nullable
    Entity b(WorldServer worldserver) {
        return this.remake(worldserver);
    }

    private Entity remake(WorldServer worldserver) {
        @Nullable ShapeDetectorShape newCoords = a(worldserver);
//        Location newCoords = UtilsCoords.fromTo(this.world.getDimensionManager(), worldserver.getDimensionManager(), this.locX(), this.locY(), this.locZ());
        if (newCoords != null) {
            // the final flags are just center correction for a block
            final NBTTagCompound oldNbt = this.save(new NBTTagCompound());
            @Nullable MobParasite newParasite = entityTypes.a(worldserver);
            if (newParasite != null) {
                newParasite.load(oldNbt);
                newParasite.setLocation(newCoords.position.x, newCoords.position.y, newCoords.position.z, newCoords.yaw, newCoords.pitch);
                newParasite.resetPortalCooldown();
                (worldserver).addEntity(newParasite);
                for (MobPartChild child : this.children) {
                    final CustomModel.CustomEntity selfModelLoc = newParasite.selfModel.getEntity();
                    EntityLocation motherLocation = new EntityLocation(
                            newParasite.getUniqueID(),
                            selfModelLoc.x,
                            selfModelLoc.y,
                            selfModelLoc.z,
                            selfModelLoc.facingX,
                            selfModelLoc.facingY,
                            selfModelLoc.facingZ
                    ); // for simpler rotations
                    newParasite.children.add(child.remake(worldserver, new MobPartMother(motherLocation, newParasite, REGISTERED_NAME)));
                }
                this.bN();
                return newParasite;
            }
        }
        this.bN();
        return null;
    }

    //todo
    @Override
    public Iterable<ItemStack> getArmorItems() {
        return Collections.emptyList();
    }

    //todo
    @Override
    public ItemStack getEquipment(EnumItemSlot enumItemSlot) {
        return ItemStack.b;
    }

    //todo
    @Override
    public void setSlot(EnumItemSlot enumItemSlot, ItemStack itemStack) {

    }

    @Override
    public EnumMainHand getMainHand() {
        return EnumMainHand.RIGHT;
    }

    private class Nav extends Navigation {
        public Nav(EntityInsentient var0, World var1) {
            super(var0, var1);
        }

        /**
         * @return should go
         */
        @Override
        protected boolean a() {
            return super.a();
        }

        /**
         * @return where to go to
         */
        @Override
        protected Vec3D b() {
            return super.b();
        }

        @Override
        @Nullable
        protected PathEntity a(Set<BlockPosition> var0, int var1, boolean var2, int var3) {
            return super.a(var0, var1, var2, var3);
        }

        @Override
        public PathEntity a(BlockPosition var0, int var1) {
            return super.a(var0, var1);
        }

        protected boolean a(Vec3D var0, Vec3D var1, int var2, int var3, int var4) {
            return super.a(var0, var1, var2, var3, var4);
        }

        @Override
        public void c() {
            super.c();

        }
    }
}
