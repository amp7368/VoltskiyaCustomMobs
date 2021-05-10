package apple.voltskiya.custom_mobs.disable;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.PathfinderGoal;

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
    public void onDisable() {
        for (Map.Entry<EntityInsentient, PathfinderGoal> mob : mobs.entrySet()) {
            if (mob != null && mob.getKey().isAlive()) {
                mob.getKey().goalSelector.a(mob.getValue());
                mob.getKey().targetSelector.a(mob.getValue());
            }
        }
    }

    @Override
    public String getName() {
        return "disable_shared";
    }
}
