package apple.voltskiya.custom_mobs.mobs;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftBlaze;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPhantom;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftZombie;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;

public class MobWarpedGremlin extends EntityZombie {
    private static EntityTypes<MobWarpedGremlin> warpedGremlinEntityType;
    private AttributeMapBase attributeMap = null;


    /**
     * constructor to match the EntityTypes requirement
     *
     * @param entitytypes my entity type. me. this is me.
     * @param world       the world to spawn the entity in
     */
    protected MobWarpedGremlin(EntityTypes<MobWarpedGremlin> entitytypes, World world) {
        super(entitytypes, world);
    }

    /**
     * registers the WarpedGremlin as an entity
     */
    public static void initialize() {
        EntityTypes.Builder<MobWarpedGremlin> entitytypesBuilder = EntityTypes.Builder.a(MobWarpedGremlin::new, EnumCreatureType.MONSTER);

        // this version of minecraft (whatever it happens to be)
        final int keyForVersion = DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion());
        // the thing to register stuff I think?
        final DataFixer dataFixerToRegister = DataConverterRegistry.a();

        final Schema schemaForSomething = dataFixerToRegister.getSchema(keyForVersion);
        final TaggedChoice.TaggedChoiceType<?> choiceType = schemaForSomething.findChoiceType(DataConverterTypes.ENTITY_TREE);

        // copy the zombie type to the warped gremlin type\
        // todo understand this more
        Map<? super Object, Type<?>> types = (Map<? super Object, Type<?>>) choiceType.types();
        final Type<?> zombieType = types.get("minecraft:zombie");
        types.put("minecraft:warped_gremlin", zombieType);

        // build it
        warpedGremlinEntityType = entitytypesBuilder.a("warped_gremlin");

        // log it
        NmsMobsPlugin.get().log(Level.INFO, "registered warped_gremlin");
    }

    /**
     * spawns a WarpedGremlin
     *
     * @param name     the name of the mob?
     * @param world    the org.bukkit world where the mob should be spawned
     * @param location the org.bukkit location where the mob should be spawned
     */
    public static void spawn(String name, org.bukkit.World world, org.bukkit.Location location) {
        final MobWarpedGremlin gremlin = new MobWarpedGremlin(warpedGremlinEntityType, ((CraftWorld) world).getHandle());
        gremlin.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        ((CraftWorld) world).getHandle().addEntity(gremlin);
    }

    /**
     * @return EnumMonsterType.ARTHROPOD || EnumMonsterType.ILLAGER || ...
     */
    @Override
    public EnumMonsterType getMonsterType() {
        return super.getMonsterType();
    }

    @Override
    public EntityTypes<?> getEntityType() {
        return EntityTypes.COW;
    }

    @Override
    public void movementTick() {
        super.movementTick();
    }

    /**
     * @return the bounding box of this entity
     */
    @Override
    public AxisAlignedBB getBoundingBox() {
        return super.getBoundingBox();
    }

    @Override
    public AttributeMapBase getAttributeMap() {
        if (this.attributeMap == null) this.attributeMap = new AttributeMapBase(getAttributeProvider());
        return this.attributeMap;
    }

    @Override
    public CraftEntity getBukkitEntity() {
        return new CraftZombie(this.getWorld().getServer(), this);
    }

    /**
     * @return the default attributeMap
     */
    private static AttributeProvider getAttributeProvider() {
        return MobWarpedGremlin.cL()
                .a(GenericAttributes.FOLLOW_RANGE, 35.0D)
                .a(GenericAttributes.MOVEMENT_SPEED, 0.23000000417232513D)
                .a(GenericAttributes.ATTACK_DAMAGE, 3.0D)
                .a(GenericAttributes.ARMOR, 2.0D)
                .a(GenericAttributes.SPAWN_REINFORCEMENTS)
                .a(GenericAttributes.ATTACK_KNOCKBACK,1D)
                .a();
    }

    @Override
    public void move(EnumMoveType enummovetype, Vec3D vec3d) {
        super.move(enummovetype, vec3d);
    }

    /**
     * @return whether the mob is in horizontal or vertical position
     */
    @Override
    public boolean bC() {
        return super.bC();
    }

    /**
     * change worlds
     */
    @Override
    public @Nullable
    Entity b(WorldServer worldserver) {
        return super.b(worldserver);
    }

    //todo
    @Override
    public Iterable<ItemStack> getArmorItems() {
        return Collections.emptyList();
    }

    //todo
    @Override
    public ItemStack getEquipment(EnumItemSlot enumItemSlot) {
        return new ItemStack(null);
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
