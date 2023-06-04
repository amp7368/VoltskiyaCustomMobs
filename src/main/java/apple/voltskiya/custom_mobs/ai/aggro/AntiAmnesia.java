package apple.voltskiya.custom_mobs.ai.aggro;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.ai.AiModule;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.jetbrains.annotations.NotNull;
import voltskiya.apple.utilities.minecraft.TagConstants;
import voltskiya.apple.utilities.minecraft.player.PlayerUtils;

public class AntiAmnesia implements Listener {

    private static final Map<UUID, AmnesiaAggro> mobs = new HashMap<>();
    private static final long NO_FORGET_QUICK = 10_000;
    private static final String ANTI_AMNESIA_TAG = AiModule.EXTENSION_TAG + ".anti_amnesia";

    public AntiAmnesia() {
        VoltskiyaPlugin.get().registerEvents(this);
    }

    @NotNull
    private synchronized static AmnesiaAggro getAggro(UUID uuid) {
        return mobs.computeIfAbsent(uuid, u -> new AmnesiaAggro());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public synchronized void onDead(EntityDeathEvent event) {
        mobs.remove(event.getEntity().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTargetChange(EntityTargetEvent event) {
        AmnesiaAggro aggro = getAggro(event.getEntity().getUniqueId());
        aggro.changeTarget();
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onForget(EntityTargetEvent event) {
        if (event.getTarget() != null) return;
        if (event.getReason() != TargetReason.FORGOT_TARGET && event.getReason() != TargetReason.TARGET_INVALID) return;
        Entity entity = event.getEntity();
        if (!entity.getScoreboardTags().contains(ANTI_AMNESIA_TAG)) return;

        System.err.println(entity.getScoreboardTags());
        boolean exception = entity.getScoreboardTags().contains(TagConstants.FORCE_TARGET);
        if (exception) return;
        if (!(event.getEntity() instanceof Mob mob)) return;
        AmnesiaAggro aggro = getAggro(entity.getUniqueId());

        LivingEntity target = mob.getTarget();
        if (target == null || target.isDead()) return;
        if (target instanceof Player player && !PlayerUtils.isSurvival(player)) return;
        if (aggro.isRecentTargetChange(NO_FORGET_QUICK)) {

            if (mob.hasLineOfSight(target)) aggro.changeTarget();
            event.setCancelled(true);
        }
    }
}
