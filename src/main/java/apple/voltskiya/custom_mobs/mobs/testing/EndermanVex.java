package apple.voltskiya.custom_mobs.mobs.testing;

import apple.voltskiya.custom_mobs.mobs.NmsMobsPlugin;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Enderman;

import java.util.Map;
import java.util.logging.Level;

public class EndermanVex extends EntityEnderman {
    public static final String REGISTERED_NAME = "enderman_vex";
    private static EntityTypes<EndermanVex> entityTypes;
    private AttributeMapBase attributeMap = null;

    public EndermanVex(EntityTypes<? extends EndermanVex> entitytypes, World world) {
        super(entitytypes, world);
    }

    /**
     * registers the WarpedGremlin as an entity
     */
    public static void initialize() {
        EntityTypes.Builder<EndermanVex> entitytypesBuilder = EntityTypes.Builder.a(EndermanVex::new, EnumCreatureType.MONSTER);
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
        final Type<?> zombieType = types.get("minecraft:enderman");
        types.put("minecraft:" + REGISTERED_NAME, zombieType);

        // build it
        entityTypes = entitytypesBuilder.a(REGISTERED_NAME);
        // log it
        NmsMobsPlugin.get().log(Level.INFO, "registered " + REGISTERED_NAME);
    }

    public static void spawn(org.bukkit.World world, Location location) {
        final EndermanVex gremlin = new EndermanVex(entityTypes, ((CraftWorld) world).getHandle());
        gremlin.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        ((CraftWorld) world).getHandle().addEntity(gremlin);
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
    public EntityTypes<?> getEntityType() {
        return EntityTypes.VEX;
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
        return EntityMonster.eR().a(GenericAttributes.MAX_HEALTH, 40.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.30000001192092896D).a(GenericAttributes.ATTACK_DAMAGE, 7.0D).a(GenericAttributes.FOLLOW_RANGE, 64.0D).a();
    }
    @Override
    public EnumMainHand getMainHand() {
        return EnumMainHand.RIGHT;
    }

}
