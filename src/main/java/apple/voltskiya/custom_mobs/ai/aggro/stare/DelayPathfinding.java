package apple.voltskiya.custom_mobs.ai.aggro.stare;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.ai.AiModule;
import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.listen.order.MMSpawningOrder;
import apple.voltskiya.mob_manager.mob.MMSpawned;

public class DelayPathfinding implements SpawnListener {


    public DelayPathfinding() {
        this.registerSpawnListener();
    }

    @Override
    public boolean isOnlyMobs() {
        return true;
    }

    @Override
    public void doSpawn(MMSpawned mmSpawned) {
        // we do the scheduling because we need to make absolutely sure we're last
        DelayPathfindingMob mob = new DelayPathfindingMob(mmSpawned);
        VoltskiyaPlugin.get().scheduleSyncDelayedTask(mob::doSpawn, 0);
    }

    @Override
    public MMSpawningOrder order() {
        return MMSpawningOrder.LATEST;
    }

    @Override
    public String getBriefTag() {
        return "stare";
    }

    @Override
    public String getExtensionTag() {
        return AiModule.EXTENSION_TAG;
    }
}
