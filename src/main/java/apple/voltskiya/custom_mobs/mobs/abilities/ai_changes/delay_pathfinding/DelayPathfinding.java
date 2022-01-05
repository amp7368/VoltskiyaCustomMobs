package apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.delay_pathfinding;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.pathfinder.DecodePathfinder;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.mobs.abilities.MobTickPlugin;
import apple.voltskiya.custom_mobs.mobs.nms.parent.config.ConfigManager;
import apple.voltskiya.custom_mobs.mobs.nms.parent.config.YmlSettings;
import apple.voltskiya.custom_mobs.mobs.nms.parent.register.RegisteredEntityEater;
import apple.voltskiya.custom_mobs.pathfinders.utilities.PathfinderGoalHurtByTargetOnDone;
import apple.voltskiya.custom_mobs.pathfinders.utilities.PathfinderGoalNearestAttackableTargetCanSee;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSelector;
import net.minecraft.world.entity.ai.goal.PathfinderGoalWrapped;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.player.EntityHuman;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;

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
    protected PluginManagedModule getPlugin() {
        return MobTickPlugin.get();
    }

    @Override
    public void eatEntity(EntityInsentient entity) {
        // we do the scheduling because we need to make sure we're last
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
            PathfinderGoalSelector oldGoalSelector = DecodeEntity.getGoalSelector(entity);
            DecodeEntity.setGoalSelector(entity, new PathfinderGoalSelector(() -> DecodeEntity.getMethodProfiler(entity)));

            PathfinderGoalSelector targetSelector = DecodeEntity.getTargetSelector(entity);
            @NotNull Collection<PathfinderGoalWrapped> removedPathfinders = DecodePathfinder.removeOfType(targetSelector, PathfinderGoalNearestAttackableTarget.class, PathfinderGoalHurtByTarget.class);
            final PathfinderGoalNearestAttackableTargetCanSee<EntityHuman> canSee = new PathfinderGoalNearestAttackableTargetCanSee<>(entity, EntityHuman.class, true);
            PathfinderGoalHurtByTargetOnDone onHurt = null;
            if (entity instanceof EntityCreature) {
                onHurt = new PathfinderGoalHurtByTargetOnDone((EntityCreature) entity);
            }
            targetSelector.a(1, canSee);
            if (onHurt != null) targetSelector.a(0, onHurt);
            PathfinderGoalHurtByTargetOnDone finalOnHurt = onHurt;
            final Runnable givePathfinding = () -> {
                targetSelector.a(canSee);
                if (finalOnHurt != null) targetSelector.a(finalOnHurt);
                DecodePathfinder.add(targetSelector, removedPathfinders);
                DecodeEntity.setGoalSelector(entity, oldGoalSelector);
            };
            canSee.addOnceOnDone(givePathfinding);
            if (onHurt != null) onHurt.addOnceOnDone(givePathfinding);
            addMob(entity.getBukkitEntity().getUniqueId());
        }, 0);
    }
}
