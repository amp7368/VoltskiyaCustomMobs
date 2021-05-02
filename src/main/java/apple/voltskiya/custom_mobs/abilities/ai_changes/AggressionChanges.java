package apple.voltskiya.custom_mobs.abilities.ai_changes;

import apple.voltskiya.custom_mobs.abilities.listeners.MobSpawnListener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Set;

public class AggressionChanges implements MobSpawnListener.SpawnModifier {

    public static final String AGGROTARGET = "aggrotarget.";

    @Override
    public void modifySpawn(CreatureSpawnEvent event) {
        Set<String> tags = event.getEntity().getScoreboardTags();
        for (String tag : tags) {
            if (tag.startsWith(AGGROTARGET)) {
                // check who this is aggressive towards
//                String aggressiveTowards = tag.substring(AGGROTARGET.length());
            }
        }
    }
}
