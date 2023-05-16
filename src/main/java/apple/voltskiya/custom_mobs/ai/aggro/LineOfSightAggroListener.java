package apple.voltskiya.custom_mobs.ai.aggro;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.ai.AiModule;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

public class LineOfSightAggroListener implements Listener {

    private static final String LOS_TAG = AiModule.EXTENSION_TAG + ".require_los";

    public LineOfSightAggroListener() {
        VoltskiyaPlugin.get().registerEvents(this);
    }

    @EventHandler
    public void onNewTarget(EntityTargetEvent event) {
        if (event.getReason() != TargetReason.CLOSEST_PLAYER) return;
        if (event.getEntity() instanceof Mob mob) {
            if (!mob.getScoreboardTags().contains(LOS_TAG)) return;
            Entity newTarget = event.getTarget();
            if (newTarget == null) return;
            if (mob.hasLineOfSight(newTarget)) return;

            event.setCancelled(true);
            if (newTarget.equals(mob.getTarget())) mob.setTarget(null);
        }
    }
}
