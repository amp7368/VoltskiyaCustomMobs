package apple.voltskiya.custom_mobs.mobs.nms.parent.holder;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.entity.DecodeEnumCreatureType;
import apple.nms.decoding.iregistry.DecodeDataConverterTypes;
import apple.nms.decoding.iregistry.DecodeIRegistry;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.mobs.PluginNmsMobs;
import apple.voltskiya.custom_mobs.mobs.SpawnCustomMobListener;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModel;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModelHandler;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.constant.EmptyPartPassthrough;
import com.mojang.datafixers.types.templates.TaggedChoice;
import net.minecraft.SharedConstants;
import net.minecraft.core.IRegistry;
import net.minecraft.core.RegistryBlocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.datafix.DataConverterRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.AttributeDefaults;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;

public class NmsMobRegisterConfigable<
        TypeEntity extends Entity & NmsMob<TypeEntity, Config>,
        Config extends NmsMobConfig<TypeEntity, Config>
        > implements SpawnCustomMobListener.CustomSpawnEater {
    private static final int failId = DecodeIRegistry.getId(DecodeIRegistry.getEntityType(), null);
    private static final TaggedChoice.TaggedChoiceType<?> choiceType;

    static {
        // this version of minecraft (whatever it happens to be)
        final int keyForVersion = DataFixUtils.makeKey(SharedConstants.b().b().c());
        // the thing to fix old versions of minecraft
        final DataFixer dataFixerToRegister = DataConverterRegistry.a();

        final Schema schemaForSomething = dataFixerToRegister.getSchema(keyForVersion);
        choiceType = schemaForSomething.findChoiceType(DecodeDataConverterTypes.ENTITY_TREE);
    }

    private final String name;
    private final EntityTypes.b<TypeEntity> mobConstructor;
    private final EntityTypes<?> replacement;
    private final Config config;
    private EntityTypes<TypeEntity> entityTypes;
    private NmsModel model;
    private NmsModelHandler.ModelConfigName modelName;


    public NmsMobRegisterConfigable(Config config) {
        this.name = config.getTag();
        this.mobConstructor = config.getEntityBuilder().builder();
        this.replacement = config.getReplacement();
        this.config = config;
        registerEntityTypes();
        registerModel(config.getModelConfigName());
        setPointers(config.getRegisterPointers());
    }

    public static Map<? super Object, Type<?>> getMinecraftTypes() {
        @SuppressWarnings("unchecked") Map<? super Object, Type<?>> types1 = (Map<? super Object, Type<?>>) choiceType.types();
        return types1;
    }

    public static void registerDataFixer(@NotNull String mobName) {
        getMinecraftTypes().putIfAbsent(mobName, new EmptyPartPassthrough());
    }

    public static <T extends Entity> EntityTypes<T> registerEntityTypesStatic(@NotNull String mobName, EntityTypes.b<T> mobConstructor, EntityTypes<?> replacement) {
        // register the  datafixer
        registerDataFixer(mobName);

        // build it
        RegistryBlocks<EntityTypes<?>> registry = DecodeIRegistry.getEntityType();

        EntityTypes.Builder<T> entitytypesBuilder = EntityTypes.Builder.a(mobConstructor, DecodeEnumCreatureType.MONSTER.encode());
        EntityTypes<T> builtEntityTypes = entitytypesBuilder.a(mobName);
        int currentId = DecodeIRegistry.getId(registry, builtEntityTypes);
        if (currentId != failId) {
            System.err.println(currentId);
            return builtEntityTypes;
        }
        // register it
        int replacementId = DecodeIRegistry.getId(registry, replacement);
        EntityTypes<T> entityTypes = IRegistry.a(registry, replacementId, mobName, builtEntityTypes);

        // log it
        PluginNmsMobs.get().log(Level.INFO, "registered " + mobName);

        return entityTypes;
    }

    private void setPointers(Collection<Consumer<NmsMobRegisterConfigable<TypeEntity, Config>>> registerPointers) {
        for (Consumer<NmsMobRegisterConfigable<TypeEntity, Config>> pointer : registerPointers) {
            pointer.accept(this);
        }
    }

    public void registerEntityTypes() {
        this.entityTypes = registerEntityTypesStatic(registeredNameId(), this.mobConstructor, replacement);
    }

    public TypeEntity spawn(Location location, NBTTagCompound oldNbt) {
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        final TypeEntity entity = mobConstructor.create(entityTypes, world);
        entity.prepare(location, oldNbt);
        entity.addChildren();
        CraftEntity bukkitEntity = entity.getBukkitEntity();
        bukkitEntity.addScoreboardTag(SpawnCustomMobListener.CUSTOM_SPAWN_COMPLETE_TAG);
        bukkitEntity.addScoreboardTag(registeredNameId());
        world.addFreshEntity(entity, CreatureSpawnEvent.SpawnReason.NATURAL);
        return entity;
    }

    @Override
    public void eatSpawnEvent(CreatureSpawnEvent event) {
        Location location = event.getEntity().getLocation();
        Entity entity = ((CraftEntity) event.getEntity()).getHandle();
        NBTTagCompound oldNbt = DecodeEntity.save(entity);
        spawn(location, oldNbt);
        event.setCancelled(true);
    }

    public void registerModel(NmsModelHandler.ModelConfigName modelName) {
        if (modelName == null) {
            this.model = null;
            this.modelName = null;
        } else {
            this.model = NmsModelHandler.parts(modelName);
            this.modelName = modelName;
        }
    }

    public EntityTypes<TypeEntity> getEntityType() {
        return this.entityTypes;
    }

    @NotNull
    public String registeredNameId() {
        return getRegisteredNamespace().toString();
    }

    @NotNull
    public MinecraftKey getRegisteredMinecraftKey() {
        NamespacedKey registeredNamespace = getRegisteredNamespace();
        return new MinecraftKey(registeredNamespace.getNamespace(), registeredNamespace.getKey());
    }

    @NotNull
    public NamespacedKey getRegisteredNamespace() {
        return new NamespacedKey(VoltskiyaPlugin.get(), this.name);
//        return new NamespacedKey("minecraft", getTag());
    }

    public AttributeProvider getAttributeProvider() {
        @SuppressWarnings("unchecked") EntityTypes<? extends EntityLiving> entityTypes = (EntityTypes<? extends EntityLiving>) this.replacement;
        return AttributeDefaults.a(entityTypes);
    }

    public NmsModel getModel() {
        return this.model;
    }

    public NmsModelHandler.ModelConfigName getModelName() {
        return this.modelName;
    }

    public String getTag() {
        return name;
    }

    public Config getConfig() {
        return config;
    }

    public EntityTypes<?> getReplacement() {
        return replacement;
    }
}
