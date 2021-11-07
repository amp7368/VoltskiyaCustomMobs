package apple.voltskiya.custom_mobs.mobs.abilities;

import apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.micro_misles.MicroMissileManager;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.lost_soul.BlemishDeathListener;
import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;
import voltskiya.apple.configs.plugin.manage.PluginManagedModuleConfig;
import voltskiya.apple.configs.plugin.saveable.ConfigSaveableBuilder;

import java.util.Collection;
import java.util.Collections;


public class MobTickPlugin extends PluginManagedModule implements PluginManagedModuleConfig {
    private static MobTickPlugin instance;


    @Override
    public void init() {
        instance = this;
    }

    @Override
    public void enable() {
        new MobTickDeathListener();
        new MobTickSpawnListener();
        new MicroMissileManager();
        new BlemishDeathListener();
    }

    public static MobTickPlugin get() {
        return instance;
    }

    @Override
    public String getName() {
        return "MobTick";
    }

    @Override
    public Collection<ConfigSaveableBuilder<?, ?, ?>> getConfigsToRegister() {
        return Collections.emptyList();
    }
}
