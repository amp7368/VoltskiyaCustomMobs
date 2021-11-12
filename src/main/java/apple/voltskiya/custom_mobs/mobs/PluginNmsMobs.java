package apple.voltskiya.custom_mobs.mobs;

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
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModelConfig;
import apple.voltskiya.custom_mobs.mobs.nms.parts.child.MobPartArmorStand;
import apple.voltskiya.custom_mobs.trash.aledar.AledarNavigation;
import apple.voltskiya.custom_mobs.trash.aledar.MobAledar;
import apple.voltskiya.custom_mobs.trash.aledar.MobCart;
import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;
import voltskiya.apple.configs.plugin.manage.PluginManagedModuleConfig;
import voltskiya.apple.configs.plugin.saveable.ConfigSaveableBuilder;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

public class PluginNmsMobs extends PluginManagedModule implements PluginManagedModuleConfig {
    private static PluginNmsMobs instance;

    @Override
    public void enable() {
        new AledarNavigation();
        new SpawnCustomMobListener();
        new SkelePacketCommand();
    }

    @Override
    public String getName() {
        return "NmsMobs";
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

        SpawnCustomMobListener.initialize();
    }

    @Override
    public Collection<ConfigSaveableBuilder<?, ?, ?>> getConfigsToRegister() {
        return Collections.emptyList();
    }
}
