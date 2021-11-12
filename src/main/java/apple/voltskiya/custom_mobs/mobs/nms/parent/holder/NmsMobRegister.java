package apple.voltskiya.custom_mobs.mobs.nms.parent.holder;

import apple.nms.decoding.entity.DecodeEnumCreatureType;
import apple.nms.decoding.iregistry.DecodeDataConverterTypes;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.mobs.PluginNmsMobs;
import apple.voltskiya.custom_mobs.mobs.SpawnCustomMobListener;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModelConfig;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.constant.EmptyPartPassthrough;
import com.mojang.datafixers.types.templates.TaggedChoice;
import net.minecraft.SharedConstants;
import net.minecraft.core.IRegistry;
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
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;

public class NmsMobRegister<
        TypeEntity extends Entity & NmsMob<TypeEntity, Config>,
        Config extends NmsMobConfig<TypeEntity, Config>
        > implements SpawnCustomMobListener.CustomSpawnEater {
    private static final TaggedChoice.TaggedChoiceType<?> choiceType;

    static {
        // this version of minecraft (whatever it happens to be)
        final int keyForVersion = DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion());
        // the thing to fix old versions of minecraft
        final DataFixer dataFixerToRegister = DataConverterRegistry.a();

        final Schema schemaForSomething = dataFixerToRegister.getSchema(keyForVersion);
        choiceType = schemaForSomething.findChoiceType(DecodeDataConverterTypes.ENTITY_TREE);
    }

    private final String name;
    private final EntityTypes.b<TypeEntity> mobConstructor;
    private final EntityTypes<?> replacement;
    private final Config config;
    private final boolean hasModel;
    private EntityTypes<TypeEntity> entityTypes;
    private NmsModelConfig model;
    private NmsModelConfig.ModelConfigName modelName;


    public NmsMobRegister(Config config) {
        this.name = config.getTag();
        this.mobConstructor = config.getEntityBuilder().builder();
        this.replacement = config.getReplacement();
        this.config = config;
        registerEntityTypes();
        this.hasModel = config.hasModel();
        if (this.hasModel)
            registerModel(config.getModelConfigName());
        setPointers(config.getRegisterPointers());
    }

    public static Map<? super Object, Type<?>> getMinecraftTypes() {
        @SuppressWarnings("unchecked") Map<? super Object, Type<?>> types1 = (Map<? super Object, Type<?>>) choiceType.types();
        return types1;
    }

    public static void registerDataFixer(@NotNull String mobName) {
        getMinecraftTypes().put(mobName, new EmptyPartPassthrough());
    }

    private void setPointers(Collection<Consumer<NmsMobRegister<TypeEntity, Config>>> registerPointers) {
        for (Consumer<NmsMobRegister<TypeEntity, Config>> pointer : registerPointers) {
            pointer.accept(this);
        }
    }

    public void registerEntityTypes() {
        // register the  datafixer
        registerDataFixer(registeredNameId());

        // build it
        EntityTypes.Builder<TypeEntity> entitytypesBuilder = EntityTypes.Builder.a(this.mobConstructor, DecodeEnumCreatureType.MONSTER.encode());
        int replacementId = IRegistry.Y.getId(replacement);
        String namespaceToRegister = registeredNameId();
        EntityTypes<TypeEntity> builtEntityTypes = entitytypesBuilder.a(registeredNameId());
        this.entityTypes = IRegistry.a(IRegistry.Y, replacementId, namespaceToRegister, builtEntityTypes);

        // log it
        PluginNmsMobs.get().log(Level.INFO, "registered " + registeredNameId());
    }

    public TypeEntity spawn(Location location, NBTTagCompound oldNbt) {
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        final TypeEntity entity = mobConstructor.create(entityTypes, world);
        entity.prepareNms(location, oldNbt);
        entity.prepare();
        entity.addChildren();
        entity.addScoreboardTag(SpawnCustomMobListener.CUSTOM_SPAWN_COMPLETE_TAG);
        entity.addScoreboardTag(registeredNameId());
        world.addEntity(entity);
        return entity;
    }

    @Override
    public void eatSpawnEvent(CreatureSpawnEvent event) {
        Location location = event.getEntity().getLocation();
        NBTTagCompound oldNbt = ((CraftEntity) event.getEntity()).getHandle().save(new NBTTagCompound());
        spawn(location, oldNbt);
        event.setCancelled(true);
    }

    public void registerModel(NmsModelConfig.ModelConfigName modelName) {
        this.model = NmsModelConfig.parts(modelName);
        this.modelName = modelName;
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

    public NmsModelConfig getModel() {
        return this.model;
    }

    public NmsModelConfig.ModelConfigName getModelName() {
        return this.modelName;
    }

    public String getTag() {
        return name;
    }

    public boolean hasModel() {
        return hasModel;
    }

    public Config getConfig() {
        return config;
    }

    public EntityTypes<?> getReplacement() {
        return replacement;
    }
}
