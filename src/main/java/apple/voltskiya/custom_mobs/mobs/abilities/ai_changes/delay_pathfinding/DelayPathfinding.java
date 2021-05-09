package apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.delay_pathfinding;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.YmlSettings;
import apple.voltskiya.custom_mobs.mobs.abilities.MobTickPlugin;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.SpawnEater;
import apple.voltskiya.custom_mobs.pathfinders.goal_selector.UtilsPathfinderGoalSelector;
import apple.voltskiya.custom_mobs.pathfinders.utilities.PathfinderGoalNearestAttackableTargetCanSee;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftMob;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class DelayPathfinding extends SpawnEater {
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
    public void eatEvent(CreatureSpawnEvent event) {
        LivingEntity e = event.getEntity();
        if (e instanceof Mob) {
            // we do the scheduling because we need to make sure we're last
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
                EntityInsentient entity = ((CraftMob) e).getHandle();
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
                addMobs(e.getUniqueId());
            }, 0);
        }
    }
}
