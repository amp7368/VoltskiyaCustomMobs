package apple.voltskiya.custom_mobs.abilities.ai_changes.revenant.trash;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.entity.DecodeEnumCreatureType;
import apple.nms.decoding.iregistry.DecodeDataConverterTypes;
import apple.nms.decoding.iregistry.DecodeIRegistry;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.mobs.PluginNmsMobs;
import apple.voltskiya.custom_mobs.mobs.SpawnCustomMobListener;
import apple.voltskiya.custom_mobs.nms.parts.NmsModel;
import apple.voltskiya.custom_mobs.nms.parts.NmsModelHandler;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.constant.EmptyPartPassthrough;
import com.mojang.datafixers.types.templates.TaggedChoice;
import net.minecraft.SharedConstants;
import net.minecraft.core.IRegistry;
import net.minecraft.core.RegistryBlocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataConverterRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
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
    TypeEntity extends Entity,
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
    private final EntityType.b<TypeEntity> mobConstructor;
    private final EntityType<?> replacement;
    private final Config config;
    private EntityType<TypeEntity> EntityType;
    private NmsModel model;
    private NmsModelHandler.ModelConfigName modelName;


    public NmsMobRegisterConfigable(Config config) {
        this.name = config.getTag();
        this.mobConstructor = config.getEntityBuilder().builder();
        this.replacement = config.getReplacement();
        this.config = config;
        registerEntityType();
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

    public static <T extends Entity> EntityType<T> registerEntityTypeStatic(@NotNull String mobName, EntityType.b<T> mobConstructor,
        EntityType<?> replacement) {
        // register the  datafixer
        registerDataFixer(mobName);

        // build it
        RegistryBlocks<EntityType<?>> registry = DecodeIRegistry.getEntityType();

        EntityType.Builder<T> EntityTypeBuilder = EntityType.Builder.a(mobConstructor, DecodeEnumCreatureType.MONSTER.encode());
        EntityType<T> builtEntityType = EntityTypeBuilder.a(mobName);
        int currentId = DecodeIRegistry.getId(registry, builtEntityType);
        if (currentId != failId) {
            return builtEntityType;
        }
        // register it
        int replacementId = DecodeIRegistry.getId(registry, replacement);
        EntityType<T> EntityType = IRegistry.a(registry, replacementId, mobName, builtEntityType);

        // log it
        PluginNmsMobs.get().log(Level.INFO, "registered " + mobName);

        return EntityType;
    }

    private void setPointers(Collection<Consumer<NmsMobRegisterConfigable<TypeEntity, Config>>> registerPointers) {
        for (Consumer<NmsMobRegisterConfigable<TypeEntity, Config>> pointer : registerPointers) {
            pointer.accept(this);
        }
    }

    public void registerEntityType() {
        this.EntityType = registerEntityTypeStatic(registeredNameId(), this.mobConstructor, replacement);
    }

    public TypeEntity spawn(Location location, CompoundTag oldNbt) {
        ServerLevel world = ((CraftWorld) location.getWorld()).getHandle();
        final TypeEntity entity = mobConstructor.create(EntityType, world);
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
        CompoundTag oldNbt = DecodeEntity.save(entity);
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

    public EntityType<TypeEntity> getEntityType() {
        return this.EntityType;
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

    public AttributeSupplier getAttributeSupplier() {
        @SuppressWarnings("unchecked") EntityType<? extends EntityLiving> EntityType = (EntityType<? extends EntityLiving>) this.replacement;
        return DefaultAttributes.a(EntityType);
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

    public EntityType<?> getReplacement() {
        return replacement;
    }
}
