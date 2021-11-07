package apple.voltskiya.custom_mobs.mobs.delay_pathfinding;

import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;

public class DelayPathfindingPlugin extends PluginManagedModule {
    @Override
    public void enable() {
        new DelayedSpawnListener();
    }

    @Override
    public String getName() {
        return "delay_pathfinding";
    }
}
