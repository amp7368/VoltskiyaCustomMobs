package apple.voltskiya.custom_mobs.mobs;

import apple.nms.decoding.iregistry.DecodeDataConverterTypes;
import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.mobs.misc.MobHealthPack;
import apple.voltskiya.custom_mobs.mobs.modified.illager.evoker.MobIllagerEvokerExaminer;
import apple.voltskiya.custom_mobs.mobs.modified.illager.illusioner.MobIllagerIllusionerExaminer;
import apple.voltskiya.custom_mobs.mobs.modified.illager.pillager.MobIllagerPillagerExaminer;
import apple.voltskiya.custom_mobs.mobs.modified.illager.vindicator.MobIllagerVindicatorExaminer;
import apple.voltskiya.custom_mobs.mobs.modified.iron_golem.MobIronGolemExaminer;
import apple.voltskiya.custom_mobs.mobs.nether.angered_soul.MobAngeredSoul;
import apple.voltskiya.custom_mobs.mobs.nether.eye_plant.MobEyePlant;
import apple.voltskiya.custom_mobs.mobs.nether.gremlin.MobWarpedGremlin;
import apple.voltskiya.custom_mobs.mobs.nether.parasite.MobParasite;
import apple.voltskiya.custom_mobs.mobs.nether.revenant.MobRevenant;
import apple.voltskiya.custom_mobs.mobs.parts.MobPartArmorStand;
import apple.voltskiya.custom_mobs.mobs.parts.NmsModelConfig;
import apple.voltskiya.custom_mobs.mobs.testing.aledar.AledarNavigation;
import apple.voltskiya.custom_mobs.mobs.testing.aledar.MobAledar;
import apple.voltskiya.custom_mobs.mobs.testing.aledar.MobCart;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import net.minecraft.SharedConstants;
import net.minecraft.util.datafix.DataConverterRegistry;

import java.io.File;
import java.util.Map;

public class PluginNmsMobs extends VoltskiyaModule {
    private static PluginNmsMobs instance;

    public static Map<? super Object, Type<?>> getMinecraftTypes() {
        // this version of minecraft (whatever it happens to be)
        final int keyForVersion = DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion());
        // the thing to register stuff I think?
        final DataFixer dataFixerToRegister = DataConverterRegistry.a();

        final Schema schemaForSomething = dataFixerToRegister.getSchema(keyForVersion);
        final TaggedChoice.TaggedChoiceType<?> choiceType = schemaForSomething.findChoiceType(DecodeDataConverterTypes.ENTITY_TREE);


        // copy the zombie type to the warped gremlin type
        Map<? super Object, Type<?>> types = (Map<? super Object, Type<?>>) choiceType.types();
        return types;
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

    public static PluginNmsMobs get() {
        return instance;
    }

    public File getModelDataFolder() {
        final File folder = new File(getDataFolder(), "models");
        if (!folder.exists()) folder.mkdirs();
        return folder;
    }

    @Override
    public void init() {
        instance = this;
        NmsModelConfig.initialize();

        MobWarpedGremlin.initialize();
        MobPartArmorStand.initialize();
        MobAledar.initialize();
        MobEyePlant.initialize();
        MobParasite.initialize();
        MobCart.initialize();
        MobIronGolemExaminer.initialize();
        MobIllagerIllusionerExaminer.initialize();
        MobIllagerPillagerExaminer.initialize();
        MobIllagerVindicatorExaminer.initialize();
        MobIllagerEvokerExaminer.initialize();
        MobRevenant.initialize();
        MobAngeredSoul.initialize();
        MobHealthPack.initialize();
    }
}
