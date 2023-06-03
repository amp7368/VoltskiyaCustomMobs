package apple.voltskiya.custom_mobs.ai.aggro.stare;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.pathfinders.utilities.GoalCanSeeTarget;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.event.MMListener;
import java.util.List;
import java.util.Set;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;

public class DelayPathfindingMob implements MMListener {

    private final MMSpawned mm;
    private List<WrappedGoal> removedGoalSelectors;
    private List<WrappedGoal> removedTargetSelectors;

    public DelayPathfindingMob(MMSpawned mm) {
        this.mm = mm;
        mm.getEvents().addListener(this);
    }

    private static boolean shouldRemove(WrappedGoal wrapped) {
        return wrapped.getGoal() instanceof NearestAttackableTargetGoal<?>;
    }

    public void doSpawn() {
        Mob mob = mm.getNmsMob();
        Set<WrappedGoal> goalSelectors = mob.goalSelector.getAvailableGoals();
        removedGoalSelectors = List.copyOf(goalSelectors);
        removedGoalSelectors.forEach(goalSelectors::remove);

        Set<WrappedGoal> targetSelectors = mob.targetSelector.getAvailableGoals();
        removedTargetSelectors = targetSelectors.stream().filter(DelayPathfindingMob::shouldRemove).toList();
        removedTargetSelectors.forEach(targetSelectors::remove);

        GoalCanSeeTarget<Player> addedTargetSelector = new GoalCanSeeTarget<>(mob, Player.class);
        mob.targetSelector.addGoal(1, addedTargetSelector);
    }

    @Override
    public void onTarget(MMSpawned mm, EntityTargetEvent event) {
        if (event.getTarget() == null) return;
        finish(mm);
    }

    private void finish(MMSpawned mm) {
        VoltskiyaPlugin.get().scheduleSyncDelayedTask(this::fixAi);
        mm.getEvents().removeListener(this);
    }

    @Override
    public void onDamage(MMSpawned mm, EntityDamageEvent event) {
        finish(mm);
    }

    private void fixAi() {
        Mob mob = mm.getNmsMob();
        mob.goalSelector.getAvailableGoals().removeIf((s) -> true);
        mob.targetSelector.getAvailableGoals().removeIf((s) -> true);
        for (WrappedGoal goal : removedGoalSelectors) {
            mob.goalSelector.addGoal(goal.getPriority(), goal.getGoal());
        }
        for (WrappedGoal goal : removedTargetSelectors) {
            mob.targetSelector.addGoal(goal.getPriority(), goal.getGoal());
        }
    }
}
