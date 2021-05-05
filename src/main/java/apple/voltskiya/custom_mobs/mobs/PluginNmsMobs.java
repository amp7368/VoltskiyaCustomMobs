package apple.voltskiya.custom_mobs.mobs;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.mobs.modified.illager.illusioner.MobIllagerIllusionerExaminer;
import apple.voltskiya.custom_mobs.mobs.modified.illager.pillager.MobIllagerPillagerExaminer;
import apple.voltskiya.custom_mobs.mobs.modified.iron_golem.MobIronGolemExaminer;
import apple.voltskiya.custom_mobs.mobs.nether.eye_plant.MobEyePlant;
import apple.voltskiya.custom_mobs.mobs.nether.gremlin.MobWarpedGremlin;
import apple.voltskiya.custom_mobs.mobs.nether.parasite.MobParasite;
import apple.voltskiya.custom_mobs.mobs.nether.revenant.MobRevenant;
import apple.voltskiya.custom_mobs.mobs.parts.MobPartArmorStand;
import apple.voltskiya.custom_mobs.mobs.parts.NmsModelConfig;
import apple.voltskiya.custom_mobs.mobs.testing.MobEndermanVex;
import apple.voltskiya.custom_mobs.mobs.testing.MobPiglinVex;
import apple.voltskiya.custom_mobs.mobs.testing.MobZombieCow;
import apple.voltskiya.custom_mobs.mobs.testing.aledar.AledarNavigation;
import apple.voltskiya.custom_mobs.mobs.testing.aledar.MobAledar;
import apple.voltskiya.custom_mobs.mobs.testing.aledar.MobCart;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import net.minecraft.server.v1_16_R3.*;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class PluginNmsMobs extends VoltskiyaModule {
    private static PluginNmsMobs instance;

    public static PluginNmsMobs get() {
        return instance;
    }

    public static Map<? super Object, Type<?>> getMinecraftTypes() {
        // this version of minecraft (whatever it happens to be)
        final int keyForVersion = DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion());
        // the thing to register stuff I think?
        final DataFixer dataFixerToRegister = DataConverterRegistry.a();

        final Schema schemaForSomething = dataFixerToRegister.getSchema(keyForVersion);
        final TaggedChoice.TaggedChoiceType<?> choiceType = schemaForSomething.findChoiceType(DataConverterTypes.ENTITY_TREE);


        // copy the zombie type to the warped gremlin type
        Map<? super Object, Type<?>> types = (Map<? super Object, Type<?>>) choiceType.types();
        return types;
    }

    @Override
    public void init() {
        instance = this;
        AttributeDefaults.a();
        NmsModelConfig.initialize();
        MobZombieCow.initialize();
        MobWarpedGremlin.initialize();
        MobPartArmorStand.initialize();
        MobPiglinVex.initialize();
        MobAledar.initialize();
        MobEndermanVex.initialize();
        MobEyePlant.initialize();
        MobParasite.initialize();
        MobCart.initialize();
        MobIronGolemExaminer.initialize();
        MobIllagerIllusionerExaminer.initialize();
        MobIllagerPillagerExaminer.initialize();
        MobRevenant.initialize();
    }

    private void initAttributeDefaults() {
        try {
            Field attributes = AttributeDefaults.class.getDeclaredField("b");
            attributes.setAccessible(true);
            Map<EntityTypes<? extends EntityLiving>, AttributeProvider> attributeMap = (Map<EntityTypes<? extends EntityLiving>, AttributeProvider>) attributes.get(null);
            attributeMap = new HashMap<>(attributeMap);
            setFinalStatic(attributes, attributeMap);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private static void setFinalStatic(Field field, Object newValue) throws NoSuchFieldException, IllegalAccessException {
        field.setAccessible(true);

        Field modifiersField = field.getClass().getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }

    @Override
    public void enable() {
        new AledarNavigation();
        new MobsSpawnCommand();
        new SpawnCustomMobListener();
    }

    @Override
    public String getName() {
        return "Mobs";
    }

    public File getModelDataFolder() {
        final File folder = new File(getDataFolder(), "models");
        if (!folder.exists()) folder.mkdirs();
        return folder;
    }
}
