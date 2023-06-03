package apple.voltskiya.custom_mobs.ai.aggro.spread;

import apple.voltskiya.custom_mobs.ai.AiModule;
import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.mob.MMSpawned;

public class AiSpread implements SpawnListener {

    public static String AI_SPREAD_PREFIX;

    public AiSpread() {
        AI_SPREAD_PREFIX = getTag() + ".";
        registerSpawnListener();
    }

    @Override
    public void doSpawn(MMSpawned mm) {
        new AiSpreadMob(mm);
    }

    @Override
    public boolean isOnlyMobs() {
        return true;
    }

    @Override
    public String getExtensionTag() {
        return AiModule.EXTENSION_TAG;
    }

    @Override
    public String getBriefTag() {
        return "spread";
    }
}
