package apple.voltskiya.custom_mobs.reload;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.mobs.RegisteredCustomMob;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import voltskiya.apple.utilities.util.constants.TagConstants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PluginDisable extends VoltskiyaModule {
    private static final Map<EntityInsentient, PathfinderGoal> mobs = new HashMap<>();
    private static int i = 0;

    public static synchronized void addMob(EntityInsentient mob, PathfinderGoal pathfinder) {
        mobs.put(mob, pathfinder);
        if (i++ % 100 == 0) {
            Iterator<Map.Entry<EntityInsentient, PathfinderGoal>> iterator = mobs.entrySet().iterator();
            if (iterator.hasNext()) {
                if (!iterator.next().getKey().isAlive())
                    iterator.remove();
            }
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
            if (mob != null && mob.getKey().isAlive()) {
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
