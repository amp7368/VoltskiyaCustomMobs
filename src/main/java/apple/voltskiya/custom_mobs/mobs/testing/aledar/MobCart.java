package apple.voltskiya.custom_mobs.mobs.testing.aledar;

import apple.voltskiya.custom_mobs.mobs.PluginNmsMobs;
import apple.voltskiya.custom_mobs.mobs.SpawnCustomMobListener;
import apple.voltskiya.custom_mobs.mobs.parts.*;
import apple.voltskiya.custom_mobs.mobs.utils.UtilsPacket;
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
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

public class MobCart extends EntityHorse {
    public static final NmsModelConfig.ModelConfigName REGISTERED_MODEL = NmsModelConfig.ModelConfigName.CART;
    public static final String REGISTERED_NAME = REGISTERED_MODEL.getFile();
    private static EntityTypes<MobCart> entityTypes;
    private NmsModelEntityConfig selfModel;
    private EntityTypes<?> selfModelType;
    private final List<MobPartChild> children = new ArrayList<>();

    public MobCart(EntityTypes<MobCart> var0, World world) {
        super(EntityTypes.HORSE, world);
    }

    /**
     * registers the WarpedGremlin as an entity
     */
    public static void initialize() {
        EntityTypes.Builder<MobCart> entitytypesBuilder = EntityTypes.Builder.a(MobCart::new, EnumCreatureType.MONSTER);

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
        final MobCart gremlin = new MobCart(entityTypes, world.getHandle());
        gremlin.prepare(location, oldNbt);
        gremlin.addScoreboardTag(SpawnCustomMobListener.CUSTOM_SPAWN_COMPLETE_TAG);
        world.getHandle().addEntity(gremlin);
    }

    public static void spawnEat(CreatureSpawnEvent event) {
        Location location = event.getEntity().getLocation();
        spawn(location, ((CraftEntity) event.getEntity()).getHandle().save(new NBTTagCompound()));
        event.setCancelled(true);
    }


    private void prepare(Location location, NBTTagCompound oldNbt) {
        final NmsModelConfig model = NmsModelConfig.parts(REGISTERED_MODEL);
        this.selfModel = model.mainPart();
        final NBTTagCompound newNbt = this.selfModel.getEntity().nbt;
        final NBTTagCompound mergedNbt = oldNbt == null ? newNbt : oldNbt.a(newNbt);
        this.loadData(mergedNbt);
        this.setInvisible(true);
        this.persistentInvisibility = true;
        this.setTamed(true);
        this.setBaby(true);
        this.ageLocked = true;
        this.saddle(SoundCategory.NEUTRAL);
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
            MobPartMother motherMe = new MobPartMother(motherLocation, this, REGISTERED_NAME);
            for (NmsModelEntityConfig part : model.others()) {
                children.add(MobParts.spawnMobPart(motherMe, part));
            }
        } else {
            this.selfModelType = EntityTypes.AREA_EFFECT_CLOUD;
            this.die();
        }
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    protected void initPathfinder() {
        // do no pathfinding
    }

    @Override
    public void move(EnumMoveType enummovetype, Vec3D vec3d) {
        super.move(enummovetype, vec3d);
        List<Packet<?>> packetsToSend = new ArrayList<>();
        for (MobPartChild child : children) {
            packetsToSend.add(child.moveFromMother(false));
        }
        UtilsPacket.sendPacketsToNearbyPlayers(packetsToSend,this.getBukkitEntity().getLocation());
    }

    @Override
    public void die() {
        super.die();
        for (MobPartChild child : children) {
            child.die();
        }
    }


    @Override
    protected SoundEffect getSoundAmbient() {
        return super.getSoundAmbient();
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource var0) {
        return super.getSoundHurt(var0);
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return super.getSoundDeath();
    }
}
