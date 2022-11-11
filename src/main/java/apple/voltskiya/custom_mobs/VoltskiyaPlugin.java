package apple.voltskiya.custom_mobs;

import apple.lib.pmc.AppleModule;
import apple.lib.pmc.ApplePlugin;
import apple.voltskiya.custom_mobs.abilities.MobTickPlugin;
import apple.voltskiya.custom_mobs.delay_pathfinding.DelayPathfindingPlugin;
import apple.voltskiya.custom_mobs.reload.PluginEnable;
import apple.voltskiya.custom_mobs.util.PluginUtils;
import apple.voltskiya.custom_mobs.util.ticking.Ticking;
import java.util.Collection;
import java.util.List;


public class VoltskiyaPlugin extends ApplePlugin {

    private static VoltskiyaPlugin instance;

    public VoltskiyaPlugin() {
        instance = this;
    }

    public static VoltskiyaPlugin get() {
        return instance;
    }

    @Override
    public Collection<AppleModule> getModules() {
        return List.of(new Ticking(), // this has to go first
            new PluginUtils(), new MobTickPlugin(), new PluginEnable(),
            new DelayPathfindingPlugin());
    }
}
