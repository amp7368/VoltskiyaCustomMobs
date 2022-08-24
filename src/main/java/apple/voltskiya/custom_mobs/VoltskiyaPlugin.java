package apple.voltskiya.custom_mobs;

import apple.lib.pmc.PluginModule;
import apple.mc.utilities.ApplePluginUtil;
import apple.voltskiya.custom_mobs.mobs.abilities.MobTickPlugin;
import apple.voltskiya.custom_mobs.mobs.delay_pathfinding.DelayPathfindingPlugin;
import apple.voltskiya.custom_mobs.reload.PluginEnable;
import apple.voltskiya.custom_mobs.util.PluginUtils;
import apple.voltskiya.custom_mobs.util.ticking.Ticking;
import java.util.Collection;
import java.util.List;


public class VoltskiyaPlugin extends ApplePluginUtil {

    private static VoltskiyaPlugin instance;

    public VoltskiyaPlugin() {
        instance = this;
    }

    public static VoltskiyaPlugin get() {
        return instance;
    }

    @Override
    public Collection<PluginModule> getModules() {
        return List.of(new Ticking(), // this has to go first
            new PluginUtils(), new MobTickPlugin(), new PluginEnable(),
            new DelayPathfindingPlugin(), new Snowball());
    }
}
