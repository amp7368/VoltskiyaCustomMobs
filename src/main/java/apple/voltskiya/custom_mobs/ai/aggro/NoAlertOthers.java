package apple.voltskiya.custom_mobs.ai.aggro;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.ai.AiModule;
import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import java.util.Set;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;

public class NoAlertOthers implements SpawnListener {

    public NoAlertOthers() {
        registerSpawnListener();
    }

    @Override
    public void doSpawn(MMSpawned mm) {
        Mob mob = mm.getNmsMob();
        VoltskiyaPlugin.get().scheduleSyncDelayedTask(() -> {
            GoalSelector selector = mob.targetSelector;
            checkGoals(selector.getAvailableGoals());
        });
    }

    @Override
    public String getExtensionTag() {
        return AiModule.EXTENSION_TAG;
    }

    private void checkGoals(Set<WrappedGoal> goals) {
        goals.forEach((goal) -> {
            if (goal.getGoal() instanceof HurtByTargetGoal hurt) {
                Class<?> ignoreAlerting = Entity.class;
                hurt.setAlertOthers(ignoreAlerting); // should be named setIgnoreAlertOthers
            }
        });
    }

    @Override
    public boolean isOnlyMobs() {
        return true;
    }

    @Override
    public String getBriefTag() {
        return "no_alert_others";
    }
}
