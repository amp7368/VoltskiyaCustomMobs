package apple.voltskiya.custom_mobs.pathfinders;

import apple.mc.utilities.world.vector.VectorUtils;
import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.entity.DecodeNavigation;
import apple.nms.decoding.pathfinder.DecodeMoveType;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import java.util.EnumSet;
import java.util.Random;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class PathfinderGoalApproachSlowly extends Goal {

    public static final int CHECK_INTERVAL = 80;
    private final Mob me;
    private final Runnable runAfterClose;
    private final Random random = new Random();
    private final int speed;
    private final double approachedDistance;
    private boolean isRunning = false;

    /**
     * find a block to navigate to
     *
     * @param me the entity to navigate
     */
    public PathfinderGoalApproachSlowly(Mob me, int speed, double approachedDistance,
        Runnable runAfterClose) {
        this.me = me;
        this.speed = speed;
        this.runAfterClose = runAfterClose;
        this.approachedDistance = approachedDistance;
        this.setFlags(EnumSet.of(DecodeMoveType.MOVE.encode()));
    }

    @Override
    public boolean canUse() {
        if (this.random.nextInt(CHECK_INTERVAL) != 0) {
            return false;
        }
        final LivingEntity goalTarget = DecodeEntity.getLastTarget(this.me);
        if (!(goalTarget instanceof Player player))
            return false;
        if (!this.isCorrectDistance(goalTarget.getBukkitEntity()))
            return false;
        if (player.getBukkitEntity().getGameMode() != GameMode.SURVIVAL) {
            return false;
        }
        this.isRunning = true;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.isRunning;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }


    private boolean isCorrectDistance(Entity goalTarget) {
        boolean isCorrect =
            this.approachedDistance < VectorUtils.distance(this.me.getBukkitEntity().getLocation(),
                goalTarget.getLocation());
        if (isCorrect)
            return true;
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), runAfterClose);
        return false;
    }


    @Override
    public void tick() {
        // go to the location
        LivingEntity goalTarget = DecodeEntity.getLastTarget(this.me);
        if (goalTarget == null) {
            this.isRunning = false;
        } else {
            PathNavigation navigation = DecodeEntity.getNavigation(this.me);
            if (isCorrectDistance(goalTarget.getBukkitEntity())) {
                Location goalLoc = goalTarget.getBukkitEntity().getLocation();
                navigation.createPath(goalLoc.getX(), goalLoc.getY(), goalLoc.getZ(), speed);
            } else {
                DecodeNavigation.cancelNavigation(navigation);
                this.isRunning = false;
            }
        }
    }

    @Override
    public void stop() {
        // quit going to the location
        DecodeNavigation.cancelNavigation(DecodeEntity.getNavigation(this.me));
    }
}
