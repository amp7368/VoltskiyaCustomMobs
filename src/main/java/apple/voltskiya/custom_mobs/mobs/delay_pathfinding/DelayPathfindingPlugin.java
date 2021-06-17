package apple.voltskiya.custom_mobs.mobs.delay_pathfinding;

import apple.voltskiya.custom_mobs.VoltskiyaModule;

public class DelayPathfindingPlugin extends VoltskiyaModule {
    @Override
    public void enable() {
        new DelayedSpawnListener();
    }

    @Override
    public String getName() {
        return "delay_pathfinding";
    }
}
