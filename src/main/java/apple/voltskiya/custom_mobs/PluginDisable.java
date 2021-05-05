package apple.voltskiya.custom_mobs;

import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.PathfinderGoal;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PluginDisable extends VoltskiyaModule {
    private static Map<UUID, PathfinderGoal> mobs = new HashMap<>();

    public static synchronized void addMob(UUID mob, PathfinderGoal pathfinder) {
        mobs.put(mob, pathfinder);
    }

    @Override
    public void enable() {

    }

    @Override
    public void onDisable() {
        for (Map.Entry<UUID, PathfinderGoal> mobPathfinder : mobs.entrySet()) {
            Entity mob = Bukkit.getEntity(mobPathfinder.getKey());
            if (mob != null) {
                net.minecraft.server.v1_16_R3.Entity entity = ((CraftEntity) mob).getHandle();
                if (entity instanceof EntityInsentient) {
                    ((EntityInsentient) entity).goalSelector.a(mobPathfinder.getValue());
                    ((EntityInsentient) entity).targetSelector.a(mobPathfinder.getValue());
                }
            }
        }
    }

    @Override
    public String getName() {
        return "disable_shared";
    }
}
