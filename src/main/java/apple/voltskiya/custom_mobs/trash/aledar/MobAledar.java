package apple.voltskiya.custom_mobs.trash.aledar;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.entity.DecodeEnumCreatureType;
import apple.nms.decoding.entity.DecodeEnumMainHand;
import apple.nms.decoding.entity.DecodeEnumMonsterType;
import apple.nms.decoding.iregistry.DecodeDataConverterTypes;
import apple.nms.decoding.iregistry.DecodeEntityTypes;
import apple.voltskiya.custom_mobs.mobs.PluginNmsMobs;
import apple.voltskiya.custom_mobs.mobs.RegisteredCustomMob;
import apple.voltskiya.custom_mobs.mobs.nms.parts.*;
import apple.voltskiya.custom_mobs.mobs.nms.utils.UtilsPacket;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.datafix.DataConverterRegistry;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPillager;
import voltskiya.apple.utilities.util.EntityLocation;

import java.util.*;
import java.util.logging.Level;

public class MobAledar extends EntityPillager implements RegisteredCustomMob {
    private static final NmsModelConfig.ModelConfigName REGISTERED_MODEL = NmsModelConfig.ModelConfigName.ALEDAR_CART;
    private static final String REGISTERED_NAME = REGISTERED_MODEL.getFile();
    public static final String IS_WHEEL_RIGHT_IDENTIFIER = "isRightWheel";
    public static final String IS_WHEEL_LEFT_IDENTIFIER = "isLeftWheel";
    public static final double SMALL_STAND_HEAD_RADIUS = .625 / 2 / 2;
    public static EntityTypes<MobAledar> mobAledarEntityType;
    private final List<MobPartChild> children = new ArrayList<>();
    private final List<MobPartArmorStand> leftWheels = new ArrayList<>();
    private final List<MobPartArmorStand> rightWheels = new ArrayList<>();

    /**
     * registers the Aledar as an entity
     */
    public static void initialize() {
        EntityTypes.Builder<MobAledar> entitytypesBuilder = EntityTypes.Builder.a(MobAledar::new, DecodeEnumCreatureType.MONSTER.encode());

        // this version of minecraft (whatever it happens to be)
        final int keyForVersion = DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion());
        // the thing to register stuff I think?
        final DataFixer dataFixerToRegister = DataConverterRegistry.a();

        final Schema schemaForSomething = dataFixerToRegister.getSchema(keyForVersion);
        final TaggedChoice.TaggedChoiceType<?> choiceType = schemaForSomething.findChoiceType(DecodeDataConverterTypes.ENTITY_TREE);

        // copy the zombie type to the warped gremlin type
        Map<? super Object, Type<?>> types = (Map<? super Object, Type<?>>) choiceType.types();
        final Type<?> pillagerType = types.get("minecraft:pillager");
        types.put("minecraft:" + REGISTERED_NAME, pillagerType);

        // build it
        mobAledarEntityType = entitytypesBuilder.a(REGISTERED_NAME);

        // log it
        PluginNmsMobs.get().log(Level.INFO, "registered " + REGISTERED_NAME);
    }

    /**
     * constructor to match the EntityTypes requirement
     *
     * @param world the world to spawn the entity in
     */
    public MobAledar(EntityTypes<? extends EntityPillager> entitytypes, World world) {
        super(DecodeEntityTypes.PILLAGER, world);
    }

    /**
     * spawns an Aledar_Cart
     *
     * @param world    the org.bukkit world where the mob should be spawned
     * @param location the org.bukkit location where the mob should be spawned
     */
    public static void spawn(org.bukkit.World world, org.bukkit.Location location) {
        final MobAledar aledar = new MobAledar(mobAledarEntityType, ((CraftWorld) world).getHandle());
        aledar.prepare(location);
        ((CraftWorld) world).getHandle().addEntity(aledar);
    }

    private void prepare(Location location) {
        final NmsModelConfig model = NmsModelConfig.parts(REGISTERED_MODEL);
        NmsModelEntityConfig selfModel = model.mainPart();
        this.loadData(selfModel.getEntity().nbt);
        this.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        final Optional<EntityTypes<?>> entityTypes = EntityTypes.a(selfModel.getEntity().type.getKey().getKey());
        if (entityTypes.isPresent()) {
            EntityLocation motherLocation = new EntityLocation(
                    this.getUniqueID(),
                    selfModel.getEntity().x,
                    selfModel.getEntity().y,
                    selfModel.getEntity().z,
                    selfModel.getEntity().facingX,
                    selfModel.getEntity().facingY,
                    selfModel.getEntity().facingZ
            ); // for simpler rotations
            MobPartMother motherMe = new MobPartMother(motherLocation, this, REGISTERED_NAME);
            for (NmsModelEntityConfig part : model.others()) {
                final MobPartChild partChild = MobParts.spawnMobPart(motherMe, part);
                children.add(partChild);
                if (((Boolean) part.getEntity().otherData.getOrDefault(IS_WHEEL_RIGHT_IDENTIFIER, false)) && partChild instanceof MobPartArmorStand) {
                    rightWheels.add((MobPartArmorStand) partChild);
                }
                if (((Boolean) part.getEntity().otherData.getOrDefault(IS_WHEEL_LEFT_IDENTIFIER, false)) && partChild instanceof MobPartArmorStand) {
                    leftWheels.add((MobPartArmorStand) partChild);
                }
            }
            this.setInvisible(false);
        } else {
            this.die();
        }
    }

    /**
     * @return the default attributeMap
     */
    private static AttributeProvider getAttributeProvider() {
        return EntityMonster.fB().a();
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        final boolean invisible = nbttagcompound.getBoolean("Invisible");
        ((CraftPillager) getBukkitEntity()).setInvisible(invisible);
        this.setInvisible(invisible);
    }

    @Override
    protected void initPathfinder() {
        // mostly a villager

        PathfinderGoalSelector goalSelector = DecodeEntity.getGoalSelector(this);
        goalSelector.a(0, new PathfinderGoalFloat(this));
        goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityZombie.class, 8.0F, 0.5D, 0.5D));
        goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityEvoker.class, 12.0F, 0.5D, 0.5D));
        goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityVindicator.class, 8.0F, 0.5D, 0.5D));
        goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityVex.class, 8.0F, 0.5D, 0.5D));
        goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityPillager.class, 15.0F, 0.5D, 0.5D));
        goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityIllagerIllusioner.class, 12.0F, 0.5D, 0.5D));
        goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityZoglin.class, 10.0F, 0.5D, 0.5D));
        goalSelector.a(1, new PathfinderGoalPanic(this, 0.5D));
        goalSelector.a(4, new PathfinderGoalMoveTowardsRestriction(this, 0.35D));
        goalSelector.a(8, new PathfinderGoalRandomStrollLand(this, 0.35D));
        goalSelector.a(9, new PathfinderGoalInteract(this, EntityHuman.class, 3.0F, 1.0F));
        goalSelector.a(10, new PathfinderGoalLookAtPlayer(this, EntityInsentient.class, 8.0F));

        // mostly a cow
//        this.goalSelector.a(0, new PathfinderGoalFloat(this));
//        this.goalSelector.a(1, new PathfinderGoalPanic(this, 2.0D));
//        this.goalSelector.a(3, new PathfinderGoalTempt(this, 1.25D, RecipeItemStack.a(new IMaterial[]{Items.WHEAT}), false));
//        this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 1.0D));
//        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
//        this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
    }

    @Override
    public EntityTypes<?> getEntityType() {
        return mobAledarEntityType;
    }

    @Override
    public void movementTick() {
        super.movementTick();
        List<Packet<?>> packetsToSend = new ArrayList<>();
        for (MobPartChild child : children) {
            packetsToSend.add(child.moveFromMother(false));
        }
        UtilsPacket.sendPacketsToNearbyPlayers(packetsToSend, this.getBukkitEntity().getLocation());
    }

    @Override
    public void move(EnumMoveType enummovetype, Vec3D vec3d) {
        double moveAmount = vec3d.g();
//      move/(pi*2*r)*360; r = .625/2/2
        moveAmount *= 360;
        moveAmount /= Math.PI * 2 * SMALL_STAND_HEAD_RADIUS;
        if (moveAmount > 3) {
            // turn the wheels!
            for (MobPartArmorStand wheel : leftWheels) {
                wheel.rotateHead(0f, 0f, (float) -moveAmount);
            }
            for (MobPartArmorStand wheel : rightWheels) {
                wheel.rotateHead(0f, 0f, (float) moveAmount);
            }
        }
        super.move(enummovetype, vec3d);
    }

    /**
     * @return EnumMonsterType.ARTHROPOD || EnumMonsterType.ILLAGER || ...
     */
    @Override
    public EnumMonsterType getMonsterType() {
        return DecodeEnumMonsterType.ILLAGER.encode();
    }

    @Override
    public void a(RemovalReason removalReason) {
        super.a(removalReason);
        for (MobPartChild child : children) {
            child.die();
        }
    }

    @Override
    public void collide(Entity entity) {
        // do absolutely nothing because I don't attack
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return Collections.emptyList();
    }

    @Override
    public ItemStack getEquipment(EnumItemSlot enumItemSlot) {
        return ItemStack.b;
    }

    @Override
    public void setSlot(EnumItemSlot enumItemSlot, ItemStack itemStack) {
    }

    @Override
    public EnumMainHand getMainHand() {
        return DecodeEnumMainHand.RIGHT.encode();
    }
}
