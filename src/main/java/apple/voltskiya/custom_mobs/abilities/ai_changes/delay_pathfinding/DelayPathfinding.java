package apple.voltskiya.custom_mobs.abilities.ai_changes.delay_pathfinding;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.pathfinder.DecodePathfinder;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.pathfinders.utilities.PathfinderGoalHurtByTargetOnDone;
import apple.voltskiya.custom_mobs.pathfinders.utilities.PathfinderGoalNearestAttackableTargetCanSee;
import apple.voltskiya.mob_manager.listen.MMSpawnListener;
import apple.voltskiya.mob_manager.listen.SpawnHandlerListener;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import java.util.Collection;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class DelayPathfinding implements SpawnHandlerListener {

    public DelayPathfinding() {
        MMSpawnListener.get().addListener(this);
    }

    @Override
    public boolean isOnlyMobs() {
        return true;
    }

    @Override
    public void handle(MMSpawned mmSpawned) {
        Mob entity = mmSpawned.getNmsMob();
        // we do the scheduling because we need to make sure we're last
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
            GoalSelector oldGoalSelector = DecodeEntity.getGoalSelector(entity);
            DecodeEntity.setGoalSelector(entity,
                new GoalSelector(() -> DecodeEntity.getMethodProfiler(entity)));

            GoalSelector targetSelector = DecodeEntity.getTargetSelector(entity);
            @NotNull Collection<WrappedGoal> removedPathfinders = DecodePathfinder.removeOfType(
                targetSelector, NearestAttackableTargetGoal.class, HurtByTargetGoal.class);
            final PathfinderGoalNearestAttackableTargetCanSee<Player> canSee = new PathfinderGoalNearestAttackableTargetCanSee<>(
                entity, Player.class, true);
            PathfinderGoalHurtByTargetOnDone onHurt = null;
            if (entity instanceof PathfinderMob) {
                onHurt = new PathfinderGoalHurtByTargetOnDone((PathfinderMob) entity);
            }
            targetSelector.addGoal(1, canSee);
            if (onHurt != null)
                targetSelector.addGoal(0, onHurt);
            PathfinderGoalHurtByTargetOnDone finalOnHurt = onHurt;
            final Runnable givePathfinding = () -> {
                targetSelector.removeGoal(canSee);
                if (finalOnHurt != null)
                    targetSelector.removeGoal(finalOnHurt);
                DecodePathfinder.add(targetSelector, removedPathfinders);
                DecodeEntity.setGoalSelector(entity, oldGoalSelector);
            };
            canSee.addOnceOnDone(givePathfinding);
            if (onHurt != null)
                onHurt.addOnceOnDone(givePathfinding);
        }, 0);
    }

    @Override
    public String getTag() {
        return "guard_stare";
    }


}
