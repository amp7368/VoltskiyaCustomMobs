package apple.voltskiya.custom_mobs.reload;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.mobs.nms.parent.register.RegisteredCustomMob;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;
import voltskiya.apple.utilities.util.constants.TagConstants;

import java.util.HashMap;
import java.util.Map;

public class PluginDisable extends PluginManagedModule {
    private static final Map<EntityInsentient, PathfinderGoal> mobs = new HashMap<>();
    private static int i = 0;

    public static synchronized void addMob(EntityInsentient mob, PathfinderGoal pathfinder) {
        mobs.put(mob, pathfinder);
        if (i++ % 100 == 0) {
            mobs.entrySet().removeIf(pair -> pair.getKey().getBukkitEntity().isDead());
        }
    }

    @Override
    public void enable() {
    }

    @Override
    public String getName() {
        return "disable_shared";
    }

    @Override
    public void onDisable() {
        for (Map.Entry<EntityInsentient, PathfinderGoal> mob : mobs.entrySet()) {
            if (mob != null && !mob.getKey().getBukkitEntity().isDead()) {
                DecodeEntity.getGoalSelector(mob.getKey()).a(mob.getValue());
                DecodeEntity.getTargetSelector(mob.getKey()).a(mob.getValue());
            }
        }
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                entity.removeScoreboardTag(TagConstants.isDoingAbility);
                net.minecraft.world.entity.Entity nms = ((CraftEntity) entity).getHandle();
                if (nms instanceof RegisteredCustomMob) ((RegisteredCustomMob) nms).onDisable();
            }
        }
    }
}
