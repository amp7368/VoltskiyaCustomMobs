package apple.voltskiya.custom_mobs.abilities.ai_changes.micro_misles;

import apple.voltskiya.custom_mobs.abilities.listeners.MobSpawnListener;
import apple.voltskiya.custom_mobs.mobs.pathfinders.PathfinderGoalShootMicroMissle;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class MicroMissleShooter implements MobSpawnListener.SpawnModifier {
    private static final int COOLDOWN = 20 * 8;

    @Override
    public void modifySpawn(CreatureSpawnEvent event) {
        Entity entity = ((CraftEntity) event.getEntity()).getHandle();
        if (entity instanceof EntityInsentient) {
            EntityInsentient mob = (EntityInsentient) entity;
            mob.goalSelector.a(0, new PathfinderGoalShootMicroMissle(mob, COOLDOWN));
        }
    }
}
