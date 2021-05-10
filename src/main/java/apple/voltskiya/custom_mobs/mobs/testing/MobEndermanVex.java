package apple.voltskiya.custom_mobs.mobs.testing;

import apple.voltskiya.custom_mobs.mobs.PluginNmsMobs;
import apple.voltskiya.custom_mobs.mobs.RegisteredCustomMob;
import apple.voltskiya.custom_mobs.mobs.utils.UtilsAttribute;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

import java.util.Map;
import java.util.logging.Level;

public class MobEndermanVex extends EntityEnderman implements RegisteredCustomMob {
    public static final String REGISTERED_NAME = "enderman_vex";
    private static EntityTypes<MobEndermanVex> entityTypes;
    private final AttributeMapBase attributeMap = null;

    public MobEndermanVex(EntityTypes<? extends MobEndermanVex> entitytypes, World world) {
        super(EntityTypes.ENDERMAN, world);
        UtilsAttribute.fillAttributes(this.getAttributeMap(), getAttributeProvider());
    }

    /**
     * registers the WarpedGremlin as an entity
     */
    public static void initialize() {
        EntityTypes.Builder<MobEndermanVex> entitytypesBuilder = EntityTypes.Builder.a(MobEndermanVex::new, EnumCreatureType.MONSTER);
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
        PluginNmsMobs.get().log(Level.INFO, "registered " + REGISTERED_NAME);
    }

    public static void spawn(org.bukkit.World world, Location location) {
        final MobEndermanVex gremlin = new MobEndermanVex(entityTypes, ((CraftWorld) world).getHandle());
        gremlin.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        ((CraftWorld) world).getHandle().addEntity(gremlin);
    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();
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
