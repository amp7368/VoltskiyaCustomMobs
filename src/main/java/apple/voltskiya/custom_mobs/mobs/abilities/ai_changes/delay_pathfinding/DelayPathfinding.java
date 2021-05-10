package apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.delay_pathfinding;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.mobs.ConfigManager;
import apple.voltskiya.custom_mobs.mobs.RegisteredEntityEater;
import apple.voltskiya.custom_mobs.mobs.YmlSettings;
import apple.voltskiya.custom_mobs.mobs.abilities.MobTickPlugin;
import apple.voltskiya.custom_mobs.pathfinders.goal_selector.UtilsPathfinderGoalSelector;
import apple.voltskiya.custom_mobs.pathfinders.utilities.PathfinderGoalNearestAttackableTargetCanSee;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class DelayPathfinding extends ConfigManager implements RegisteredEntityEater {
    /**
     * @return the name of the sub_module (a step below a module)
     */
    @Override
    public String getName() {
        return "guard_stare";
    }

    /**
     * @return the default values for the config file
     */
    @Override
    public YmlSettings[] getSettings() {
        return new YmlSettings[0];
    }

    /**
     * @return the module associated with this config
     */
    @Override
    protected VoltskiyaModule getPlugin() {
        return MobTickPlugin.get();
    }

    @Override
    public void eatEntity(EntityInsentient entity) {
        // we do the scheduling because we need to make sure we're last
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
            PathfinderGoalSelector oldGoalSelector = entity.goalSelector;
            entity.goalSelector = new PathfinderGoalSelector(entity.getWorld().getMethodProfilerSupplier());

            @NotNull Collection<PathfinderGoalWrapped> removedPathfinders = UtilsPathfinderGoalSelector.remove(entity.targetSelector, PathfinderGoalNearestAttackableTarget.class, PathfinderGoalHurtByTarget.class);
            final PathfinderGoalNearestAttackableTargetCanSee<EntityHuman> canSee = new PathfinderGoalNearestAttackableTargetCanSee<>(entity, EntityHuman.class, true);
            PathfinderGoalHurtByTarget onHurt = null;
            if (entity instanceof EntityCreature)
                onHurt = new PathfinderGoalHurtByTarget((EntityCreature) entity);
            entity.targetSelector.a(0, canSee);
            if (onHurt != null) entity.targetSelector.a(1, onHurt);
            PathfinderGoalHurtByTarget finalOnHurt = onHurt;
            canSee.addOnceOnDone(() -> {
                entity.targetSelector.a(canSee);
                if (finalOnHurt != null) entity.targetSelector.a(finalOnHurt);
                UtilsPathfinderGoalSelector.add(entity.targetSelector, removedPathfinders);
                entity.goalSelector = oldGoalSelector;
            });
            addMob(entity.getUniqueID());
        }, 0);
    }
}
