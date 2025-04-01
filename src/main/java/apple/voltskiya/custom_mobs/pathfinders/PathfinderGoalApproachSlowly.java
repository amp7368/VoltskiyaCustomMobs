package apple.voltskiya.custom_mobs.pathfinders;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import java.util.EnumSet;
import java.util.Random;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import voltskiya.apple.utilities.minecraft.player.PlayerUtils;

public class PathfinderGoalApproachSlowly extends Goal {

    public static final int PROBABILITY = 80;
    private final Mob mob;
    private final Runnable runAfterClose;
    private final Random random = new Random();
    private final int speed;
    private final double approachedDistance;
    private boolean isRunning = false;

    /**
     * find a block to navigate to
     *
     * @param mob the entity to navigate
     */
    public PathfinderGoalApproachSlowly(Mob mob, int speed, double approachedDistance,
        Runnable runAfterClose) {
        this.mob = mob;
        this.speed = speed;
        this.runAfterClose = runAfterClose;
        this.approachedDistance = approachedDistance;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.random.nextInt(PROBABILITY) != 0) return false;

        final LivingEntity goalTarget = this.mob.getTarget();
        if (goalTarget == null) return false;
        CraftEntity bukkit = goalTarget.getBukkitEntity();
        if (!this.isFarFromTarget(bukkit)) {
            runStop();
            return false;
        }
        if (bukkit instanceof Player player && !PlayerUtils.isSurvival(player)) {
            return false;
        }
        return this.isRunning = true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.isRunning;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }


    private boolean isFarFromTarget(Entity goalTarget) {
        double distance = this.mob.getBukkitEntity().getLocation().distance(goalTarget.getLocation());
        return this.approachedDistance < distance;
    }


    @Override
    public void tick() {
        // go to the location
        LivingEntity goalTarget = this.mob.getTarget();
        if (goalTarget == null) {
            this.isRunning = false;
            return;
        }
        CraftEntity bukkitTarget = goalTarget.getBukkitEntity();
        PathNavigation navigation = this.mob.getNavigation();
        if (isFarFromTarget(bukkitTarget)) {
            Location goalLoc = bukkitTarget.getLocation();
            navigation.moveTo(goalLoc.getX(), goalLoc.getY(), goalLoc.getZ(), speed);

        } else {
            this.isRunning = false;
        }
    }

    @Override
    public void stop() {
        // quit going to the location
        this.mob.getNavigation().stop();
        runStop();
    }

    private void runStop() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), runAfterClose);
    }
}
