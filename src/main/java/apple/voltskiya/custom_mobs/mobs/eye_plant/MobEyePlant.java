package apple.voltskiya.custom_mobs.mobs.eye_plant;

import apple.voltskiya.custom_mobs.mobs.NmsMobsPlugin;
import apple.voltskiya.custom_mobs.mobs.NmsModelConfig;
import apple.voltskiya.custom_mobs.mobs.NmsModelEntityConfig;
import apple.voltskiya.custom_mobs.mobs.parts.MobPartChild;
import apple.voltskiya.custom_mobs.mobs.parts.MobPartMother;
import apple.voltskiya.custom_mobs.mobs.parts.MobParts;
import apple.voltskiya.custom_mobs.mobs.utils.UtilsAttribute;
import apple.voltskiya.custom_mobs.util.EntityLocation;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftZombie;

import java.util.*;
import java.util.logging.Level;

public class MobEyePlant extends EntityZombie {
    public static final NmsModelConfig.ModelConfigName REGISTERED_MODEL = NmsModelConfig.ModelConfigName.EYE_PLANT;
    public static final String REGISTERED_NAME = REGISTERED_MODEL.getFile();
    public static final AttributeProvider ATTRIBUTE_PROVIDER = EntityZombie.eS().a();
    private static EntityTypes<MobEyePlant> warpedGremlinEntityType;
    private NmsModelEntityConfig selfModel;
    private EntityTypes<?> selfModelType;
    private final List<MobPartChild> children = new ArrayList<>();

    /**
     * constructor to match the EntityTypes requirement
     *
     * @param world the world to spawn the entity in
     */

    public MobEyePlant(EntityTypes<MobEyePlant> entityTypes, World world) {
        super(EntityTypes.ZOMBIE, world);
        UtilsAttribute.fillAttributes(this.getAttributeMap(), getAttributeProvider());
    }

    /**
     * registers the WarpedGremlin as an entity
     */
    public static void initialize() {
        EntityTypes.Builder<MobEyePlant> entitytypesBuilder = EntityTypes.Builder.a(MobEyePlant::new, EnumCreatureType.MONSTER);

        // this version of minecraft (whatever it happens to be)
        final int keyForVersion = DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion());
        // the thing to register stuff I think?
        final DataFixer dataFixerToRegister = DataConverterRegistry.a();

        final Schema schemaForSomething = dataFixerToRegister.getSchema(keyForVersion);
        final TaggedChoice.TaggedChoiceType<?> choiceType = schemaForSomething.findChoiceType(DataConverterTypes.ENTITY_TREE);

        // copy the zombie type to the warped gremlin type
        // todo understand this more
        Map<? super Object, Type<?>> types = (Map<? super Object, Type<?>>) choiceType.types();
        final Type<?> zombieType = types.get("minecraft:zombie");
        types.put("minecraft:" + REGISTERED_NAME, zombieType);

        // build it
        warpedGremlinEntityType = entitytypesBuilder.a(REGISTERED_NAME);

        // log it
        NmsMobsPlugin.get().log(Level.INFO, "registered " + REGISTERED_NAME);
    }

    /**
     * spawns a WarpedGremlin
     *
     * @param world    the org.bukkit world where the mob should be spawned
     * @param location the org.bukkit location where the mob should be spawned
     */
    public static void spawn(org.bukkit.World world, org.bukkit.Location location) {
        final MobEyePlant gremlin = new MobEyePlant(warpedGremlinEntityType, ((CraftWorld) world).getHandle());
        gremlin.prepare(location);
        ((CraftWorld) world).getHandle().addEntity(gremlin);
    }

    private void prepare(Location location) {
        final NmsModelConfig model = NmsModelConfig.parts(REGISTERED_MODEL);
        this.selfModel = model.mainPart();
        this.loadData(this.selfModel.getEntity().nbt);
        this.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        final Optional<EntityTypes<?>> entityTypes = EntityTypes.a(this.selfModel.getEntity().type.getKey().getKey());
        if (entityTypes.isPresent()) {
            this.selfModelType = entityTypes.get();
            EntityLocation motherLocation = new EntityLocation(
                    this.getUniqueID(),
                    this.selfModel.getEntity().x,
                    this.selfModel.getEntity().y,
                    this.selfModel.getEntity().z,
                    this.selfModel.getEntity().facingX,
                    this.selfModel.getEntity().facingY,
                    this.selfModel.getEntity().facingZ
            ); // for simpler rotations
            MobPartMother motherMe = new MobPartMother(motherLocation, this);
            for (NmsModelEntityConfig part : model.others()) {
                this.children.add(MobParts.spawnMobPart(motherMe, part));
            }
        } else {
            this.selfModelType = EntityTypes.AREA_EFFECT_CLOUD;
            this.die();
        }
        this.lookController = new MobParts.ControllerLookChildrenFollow(this, this.children);
    }

    @Override
    protected void initPathfinder() {
        // only look aat the player
        this.goalSelector.a(0, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        final boolean invisible = nbttagcompound.getBoolean("Invisible");
        ((CraftZombie) getBukkitEntity()).setInvisible(invisible);
        this.setInvisible(invisible);
    }

    public AttributeProvider getAttributeProvider() {
        return ATTRIBUTE_PROVIDER;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        if (random.nextBoolean()) return null;
        ambientParticles();
        final double choice = random.nextDouble();
        if (choice < 1 / 3f) {
            return SoundEffects.ENTITY_ENDERMAN_AMBIENT;
        } else if (choice < 2 / 3f) {
            return SoundEffects.BLOCK_ENDER_CHEST_CLOSE;
        } else {
            return SoundEffects.BLOCK_ENDER_CHEST_OPEN;
        }
    }

    private void ambientParticles() {
        CraftEntity me = getBukkitEntity();
        for (int i = 0; i < 10; i++) {
            Location location = me.getLocation();
            double x = location.getX();
            double y = location.getY();
            double z = location.getZ();
            double xi = random.nextDouble() - .5;
            double yi = random.nextDouble() - .5;
            double zi = random.nextDouble() - .5;
            me.getWorld().spawnParticle(org.bukkit.Particle.CRIT_MAGIC, x + xi, y + yi, z + zi, 1);
        }
    }


    @Override
    public EntityTypes<?> getEntityType() {
        return this.selfModelType;
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
    public void die() {
        super.die();
        for (MobPartChild child : children) {
            child.die();
        }
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
}
