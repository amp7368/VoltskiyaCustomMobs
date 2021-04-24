package apple.voltskiya.custom_mobs.mobs.testing;

import apple.voltskiya.custom_mobs.mobs.NmsMobsPlugin;
import apple.voltskiya.custom_mobs.mobs.NmsModelConfig;
import apple.voltskiya.custom_mobs.mobs.NmsModelEntityConfig;
import apple.voltskiya.custom_mobs.mobs.parts.MobPartChild;
import apple.voltskiya.custom_mobs.mobs.parts.MobPartMother;
import apple.voltskiya.custom_mobs.mobs.parts.MobParts;
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
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftZombie;
import org.bukkit.entity.ArmorStand;

import java.util.*;
import java.util.logging.Level;

public class MobMiscCustomModel extends EntityZombie {
    private NmsModelEntityConfig selfModel;
    private EntityTypes<?> selfModelType;
    private AttributeMapBase attributeMap = null;
    private final List<MobPartChild> children = new ArrayList<>();

    /**
     * constructor to match the EntityTypes requirement
     *
     * @param world the world to spawn the entity in
     */

    public MobMiscCustomModel(EntityTypes<MobMiscCustomModel> entityTypes, World world) {
        super(entityTypes, world);
    }

    /**
     * registers the CustomModel as an entity
     */
    public static EntityTypes<MobMiscCustomModel> register(String name) {
        Optional<EntityTypes<?>> entityTypes = EntityTypes.a(name);
        if (entityTypes.isPresent()) {
            return (EntityTypes<MobMiscCustomModel>) entityTypes.get();
        }

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
        types.put("minecraft:" + name, zombieType);

        EntityTypes.Builder<MobMiscCustomModel> entitytypesBuilder = EntityTypes.Builder.a(MobMiscCustomModel::new, EnumCreatureType.MONSTER);
        final EntityTypes<MobMiscCustomModel> entityTypesFinal = entitytypesBuilder.a(name);

        // log it
        NmsMobsPlugin.get().log(Level.INFO, "registered " + name);
        return entityTypesFinal;

    }

    /**
     * spawns a CustomModel
     *
     * @param world    the org.bukkit world where the mob should be spawned
     * @param location the org.bukkit location where the mob should be spawned
     */
    public static void spawn(org.bukkit.World world, org.bukkit.Location location, String name) {
        final MobMiscCustomModel gremlin = new MobMiscCustomModel(register(name), ((CraftWorld) world).getHandle());
        gremlin.prepare(location, name);
        ((CraftWorld) world).getHandle().addEntity(gremlin);
    }

    private void prepare(Location location, String name) {
        final NmsModelConfig model = NmsModelConfig.parts(name);
        if (model == null) {
            die();
            return;
        }
        this.selfModel = model.mainPart();
        this.loadData(this.selfModel.getEntity().nbt);
        this.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        final Optional<EntityTypes<?>> entityTypes = EntityTypes.a(this.selfModel.getEntity().type.getKey().getKey());
        if (entityTypes.isPresent()) {
            this.selfModelType = entityTypes.get();
            EntityLocation motherLocation = new EntityLocation(
                    this.getUniqueID(),
                    selfModel.getEntity().x,
                    selfModel.getEntity().y,
                    selfModel.getEntity().z,
                    selfModel.getEntity().facingX,
                    selfModel.getEntity().facingY,
                    selfModel.getEntity().facingZ
            ); // for simpler rotations
            MobPartMother motherMe = new MobPartMother(motherLocation, this);
            for (NmsModelEntityConfig part : model.others()) {
                children.add(MobParts.spawnMobPart(motherMe, part));
            }
        } else {
            this.selfModelType = EntityTypes.AREA_EFFECT_CLOUD;
            this.die();
        }
    }

    @Override
    protected void initPathfinder() {
        // only look aat the player
//        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        super.initPathfinder();
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        final boolean invisible = nbttagcompound.getBoolean("Invisible");
        ((CraftZombie) getBukkitEntity()).setInvisible(invisible);
        this.setInvisible(invisible);
    }


    @Override
    public EntityTypes<?> getEntityType() {
        return this.selfModelType;
    }

    @Override
    protected void d(DamageSource damagesource) {
        super.d(damagesource);
    }

    @Override
    public void die() {
        super.die();
        for (MobPartChild child : children) {
            child.die();
        }
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
        return EntityLiving.cL()
                .a(GenericAttributes.FOLLOW_RANGE, 35.0D)
                .a(GenericAttributes.MOVEMENT_SPEED, 0.23000000417232513D)
                .a(GenericAttributes.ATTACK_DAMAGE, 3.0D)
                .a(GenericAttributes.ARMOR, 2.0D)
                .a(GenericAttributes.SPAWN_REINFORCEMENTS)
                .a(GenericAttributes.ATTACK_KNOCKBACK, 1D)
                .a();
    }

    @Override
    public void move(EnumMoveType enummovetype, Vec3D vec3d) {
        super.move(enummovetype, vec3d);
        List<PacketPlayOutEntityStatus> packetsToSend = new ArrayList<>();
        for (MobPartChild child : children) {
            packetsToSend.add(child.moveFromMother(false));
        }
        UtilsPacket.sendPacketsToAllPlayers(packetsToSend);
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
