package apple.voltskiya.custom_mobs.pathfinders;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.entity.DecodeNavigation;
import apple.nms.decoding.pathfinder.DecodeMoveType;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.reload.PluginDisable;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import apple.mc.utilities.world.vector.VectorUtils;

import java.util.EnumSet;
import java.util.Random;

public class GoalApproachSlowly extends Goal {
    public static final int CHECK_INTERVAL = 80;
    private final Mob me;
    private final Runnable runAfterClose;
    private final Random random = new Random();
    private final double speed;
    private final double approachedDistance;
    private boolean isRunning = false;

    /**
     * find a block to navigate to
     *
     * @param me the entity to navigate
     */
    public GoalApproachSlowly(Mob me, double speed, double approachedDistance, Runnable runAfterClose) {
        this.me = me;
        this.speed = speed;
        this.runAfterClose = runAfterClose;
        this.approachedDistance = approachedDistance;
        this.setMoveType(EnumSet.of(DecodeMoveType.MOVE.encode()));
        PluginDisable.addMob(me, this);
    }

    /**
     * @return whether this pathfinder should be started
     */
    @Override
    public boolean a() {
        if (this.random.nextInt(CHECK_INTERVAL) != 0) {
            return false;
        }
        final EntityLiving goalTarget = DecodeEntity.getLastTarget(this.me);
        if (!(goalTarget instanceof EntityPlayer))
            return false;
        if (!this.isCorrectDistance(goalTarget))
            return false;
        if (((EntityPlayer) goalTarget).getBukkitEntity().getGameMode() != GameMode.SURVIVAL) {
            return false;
        }
        this.isRunning = true;
        return true;
    }

    /**
     * @return true if we should keep running. otherwise false
     */
    @Override
    public boolean b() {
        return this.isRunning;
    }

    /**
     * @return something
     */
    @Override
    public boolean D_() {
        return true;
    }


    private boolean isCorrectDistance(EntityLiving goalTarget) {
        boolean isCorrect = this.approachedDistance < VectorUtils.magnitude(this.me.getBukkitEntity().getLocation(), goalTarget.getBukkitEntity().getLocation());
        if (isCorrect) return true;
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), runAfterClose);
        return false;
    }


    /**
     * start the pathfinding
     */
    @Override
    public void c() {
    }

    @Override
public void tick() {
        // go to the location
        final EntityLiving goalTarget = DecodeEntity.getLastTarget(this.me);
        if (goalTarget == null) {
            this.isRunning = false;
        } else {
            PathNavigation navigation = DecodeEntity.getNavigation(this.me);
            if (isCorrectDistance(goalTarget)) {
                Location goalLoc = goalTarget.getBukkitEntity().getLocation();
                navigation.a(goalLoc.getX(), goalLoc.getY(), goalLoc.getZ(), speed);
            } else {
                DecodeNavigation.cancelNavigation(navigation);
                this.isRunning = false;
            }
        }
    }

    /**
     * on completion of goal, do what?
     */
    @Override
    public void d() {
        // quit going to the location
        DecodeNavigation.cancelNavigation(DecodeEntity.getNavigation(this.me));
    }

    public void setMoveType(EnumSet<Type> moveType) {
        super.a(moveType);
    }
}
