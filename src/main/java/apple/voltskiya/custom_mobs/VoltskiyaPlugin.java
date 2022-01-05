package apple.voltskiya.custom_mobs;

import apple.voltskiya.custom_mobs.custom_model.CustomModelPlugin;
import apple.voltskiya.custom_mobs.leaps.LeapPlugin;
import apple.voltskiya.custom_mobs.mobs.PluginNmsMobs;
import apple.voltskiya.custom_mobs.mobs.abilities.MobTickPlugin;
import apple.voltskiya.custom_mobs.mobs.delay_pathfinding.DelayPathfindingPlugin;
import apple.voltskiya.custom_mobs.reload.PluginDisable;
import apple.voltskiya.custom_mobs.reload.PluginEnable;
import apple.voltskiya.custom_mobs.turret.PluginTurret;
import apple.voltskiya.custom_mobs.util.PluginUtils;
import apple.voltskiya.custom_mobs.util.ticking.Ticking;
import plugin.util.plugin.plugin.util.plugin.PluginManaged;
import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;
import voltskiya.apple.configs.plugin.manage.PluginManagedConfigRegister;

import java.util.Collection;
import java.util.List;


public class VoltskiyaPlugin extends PluginManaged implements PluginManagedConfigRegister {
    private static VoltskiyaPlugin instance;

    public VoltskiyaPlugin() {
        instance = this;
    }

    @Override
    public void initialize() {
        registerAllConfigs();
    }

    public static VoltskiyaPlugin get() {
        return instance;
    }

    @Override
    public Collection<PluginManagedModule> getModules() {
        return List.of(
                new PluginDisable(),
                new Ticking(), // this has to go first
                new PluginUtils(),
                new MobTickPlugin(),
                new LeapPlugin(),
                new CustomModelPlugin(),
                new PluginNmsMobs(),
                new PluginTurret(),
                new PluginEnable(),
                new DelayPathfindingPlugin(),
                new Snowball()
        );
    }
}
