package apple.voltskiya.custom_mobs.mobs;

import apple.nms.decoding.iregistry.DecodeDataConverterTypes;
import apple.voltskiya.custom_mobs.mobs.modified.illager.evoker.MobIllagerEvokerExaminer;
import apple.voltskiya.custom_mobs.mobs.modified.illager.illusioner.MobIllagerIllusionerExaminer;
import apple.voltskiya.custom_mobs.mobs.modified.illager.pillager.MobIllagerPillagerExaminer;
import apple.voltskiya.custom_mobs.mobs.modified.illager.vindicator.MobIllagerVindicatorExaminer;
import apple.voltskiya.custom_mobs.mobs.modified.iron_golem.MobIronGolemExaminer;
import apple.voltskiya.custom_mobs.mobs.nms.misc.MobHealthPack;
import apple.voltskiya.custom_mobs.mobs.nms.misc.MobTestSkeleton;
import apple.voltskiya.custom_mobs.mobs.nms.nether.angered_soul.MobAngeredSoul;
import apple.voltskiya.custom_mobs.mobs.nms.nether.eye_plant.MobEyePlant;
import apple.voltskiya.custom_mobs.mobs.nms.nether.gremlin.MobWarpedGremlin;
import apple.voltskiya.custom_mobs.mobs.nms.nether.parasite.MobParasite;
import apple.voltskiya.custom_mobs.mobs.nms.nether.revenant.MobRevenant;
import apple.voltskiya.custom_mobs.mobs.nms.overworld.apc33.MobAPC33;
import apple.voltskiya.custom_mobs.mobs.nms.parts.MobPartArmorStand;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModelConfig;
import apple.voltskiya.custom_mobs.trash.aledar.AledarNavigation;
import apple.voltskiya.custom_mobs.trash.aledar.MobAledar;
import apple.voltskiya.custom_mobs.trash.aledar.MobCart;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import net.minecraft.SharedConstants;
import net.minecraft.util.datafix.DataConverterRegistry;
import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;

import java.io.File;
import java.util.Map;

public class PluginNmsMobs extends PluginManagedModule {
    private static PluginNmsMobs instance;
    private static final TaggedChoice.TaggedChoiceType<?> choiceType;

    static {
        // this version of minecraft (whatever it happens to be)
        final int keyForVersion = DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion(), 5);
        // the thing to register stuff I think?
        final DataFixer dataFixerToRegister = DataConverterRegistry.a();
//        dataFixerToRegister.update(DecodeDataConverterTypes.ENTITY_TREE,new Dynamic)

        final Schema schemaForSomething = dataFixerToRegister.getSchema(keyForVersion);
//        schemaForSomething.register(schemaForSomething.findChoiceType(DecodeDataConverterTypes.ENTITY_TREE).types(),"",null);
        choiceType = schemaForSomething.findChoiceType(DecodeDataConverterTypes.ENTITY_TREE);
    }

    public static Map<? super Object, Type<?>> getMinecraftTypes() {
        return (Map<? super Object, Type<?>>) choiceType.types();
    }

    @Override
    public void enable() {
        new AledarNavigation();
        new MobsSpawnCommand();
        new SpawnCustomMobListener();
        new SkelePacketCommand();
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
        MobTestSkeleton.initialize();
        MobAPC33.initialize();
    }
}
