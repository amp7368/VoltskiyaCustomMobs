package apple.voltskiya.custom_mobs.mobs.pathfinders;

import apple.voltskiya.custom_mobs.abilities.ai_changes.micro_misles.MicroMissileManager;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.PathfinderGoal;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

public class PathfinderGoalShootMicroMissle extends PathfinderGoal {
    public static final double SHOOT_FREQUENCY = .05;
    private final Random random = new Random();
    private final EntityInsentient me;
    private final int cooldown;
    private int lastShot = 0;

    public PathfinderGoalShootMicroMissle(EntityInsentient me, int cooldown) {
        this.me = me;
        this.cooldown = cooldown;
        this.a(EnumSet.of(Type.TARGET));
    }

    /**
     * @return whether I even want to consider checking whether to run
     */
    @Override
    public boolean a() {
        return random.nextFloat() < SHOOT_FREQUENCY && this.me.getGoalTarget() != null && this.me.ticksLived - this.lastShot >= cooldown;
    }

    @Override
    public void c() {
        final Location targetLocation = getTargetLocation();
        if (targetLocation != null) {
            this.lastShot = this.me.ticksLived;
            final EntityLiving goalTarget = this.me.getGoalTarget();
            MicroMissileManager.shoot(this.me.getBukkitEntity().getLocation().add(0, 1, 0), targetLocation);
//            MicroMissleCluster.spawnCluster(this.me.getBukkitEntity().getLocation().add(0, 1, 0), targetLocation, goalTarget == null ? null : (LivingEntity) goalTarget.getBukkitEntity());
        }
    }

    @Nullable
    private Location getTargetLocation() {
        final EntityLiving goalTarget = this.me.getGoalTarget();
        return goalTarget == null ? null : goalTarget.getBukkitEntity().getLocation().add(0, goalTarget.getHeadHeight(), 0);
    }
}
