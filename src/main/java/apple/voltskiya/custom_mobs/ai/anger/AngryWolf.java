package apple.voltskiya.custom_mobs.ai.anger;

import apple.voltskiya.custom_mobs.ai.AiModule;
import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Wolf;

public class AngryWolf implements SpawnListener {

    public AngryWolf() {
        this.registerSpawnListener();
    }

    @Override
    public void doSpawn(MMSpawned mmSpawned) {
        Mob mob = mmSpawned.getMob();
        if (mob instanceof Wolf wolf) {
            wolf.setAngry(true);
        }
    }

    @Override
    public boolean shouldHandle(Entity entity) {
        return entity instanceof Wolf;
    }

    @Override
    public String getExtensionTag() {
        return AiModule.EXTENSION_TAG;
    }

    @Override
    public String getBriefTag() {
        return "angry";
    }
}
