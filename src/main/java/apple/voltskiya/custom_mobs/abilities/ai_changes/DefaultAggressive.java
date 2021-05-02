package apple.voltskiya.custom_mobs.abilities.ai_changes;

import apple.voltskiya.custom_mobs.abilities.listeners.MobSpawnListener;
import net.minecraft.server.v1_16_R3.IEntityAngerable;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class DefaultAggressive implements MobSpawnListener.SpawnModifier {
    @Override
    public void modifySpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof IEntityAngerable) {
//            ((IEntityAngerable) event.getEntity()).setAnger(Integer.MAX_VALUE);
        }
    }
}
