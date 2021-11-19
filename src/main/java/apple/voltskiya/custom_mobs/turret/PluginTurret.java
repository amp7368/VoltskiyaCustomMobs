package apple.voltskiya.custom_mobs.turret;

import apple.voltskiya.custom_mobs.turret.gm.TurretMobGmConfig;
import apple.voltskiya.custom_mobs.turret.manage.TurretDatabase;
import apple.voltskiya.custom_mobs.turret.manage.TurretSpawnListener;
import apple.voltskiya.custom_mobs.turret.manage.model.config.TurretModelHandlerConfig;
import apple.voltskiya.custom_mobs.turret.player.TurretMobPlayerConfig;
import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;
import voltskiya.apple.configs.plugin.manage.ConfigBuilderHolder;
import voltskiya.apple.configs.plugin.manage.PluginManagedModuleConfig;

import java.util.Collection;
import java.util.List;

public class PluginTurret extends PluginManagedModule implements PluginManagedModuleConfig {
    private static PluginTurret instance;

    public PluginTurret() {
        instance = this;
    }


    public static PluginTurret get() {
        return instance;
    }

    @Override
    public void enable() {
        TurretDatabase.initialize();
        new TurretModelHandlerConfig();
        new TurretSpawnListener();
        TurretDatabase.loadAll();
    }

    @Override
    public String getName() {
        return "Turret";
    }

    @Override
    public Collection<ConfigBuilderHolder<?>> getConfigsToRegister() {
        return List.of(configFolder(
                        yml(TurretMobPlayerConfig.class).setName("TurretPlayerConfig"),
                        yml(TurretMobGmConfig.class).setName("TurretGmConfig")
                ).setFileToSave("config")
                        .nameAsExtension()
                        .setExtension(this::extensionYmlI)
        );
    }
}
