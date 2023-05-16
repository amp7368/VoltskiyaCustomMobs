package apple.voltskiya.custom_mobs.ai.aggro;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.ai.AiModule;
import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import java.util.stream.Stream;
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
            Class<?> mobSameType = mob.getClass();
            checkGoals(selector.getAvailableGoals().stream(), mobSameType);
            checkGoals(selector.getRunningGoals(), mobSameType);
        });
    }

    private void checkGoals(Stream<WrappedGoal> goals, Class<?> mobSameType) {
        goals.forEach((goal) -> {
            if (goal.getGoal() instanceof HurtByTargetGoal hurt) {
                hurt.setAlertOthers(mobSameType); // should be named setIgnoreAlertOthers
            }
        });
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
        return "no_alert_others";
    }
}
