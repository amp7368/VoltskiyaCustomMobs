package apple.voltskiya.custom_mobs.mobs.delay_pathfinding.stare;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import org.bukkit.Bukkit;
import org.bukkit.plugin.IllegalPluginAccessException;

public class PathfinderGoalHurtByTargetCanSee extends HurtByTargetGoal {

    private static final double DEFAULT_RADIANS = Math.toRadians(65);
    private final double fieldOfViewRadians = DEFAULT_RADIANS;
    private final Collection<Runnable> onDoneOnce = new ArrayList<>();
    private final boolean doLineOfSightPredicate = true;
    private boolean shouldDoOnDoneOnce = true;

    public PathfinderGoalHurtByTargetCanSee(PathfinderMob entity) {
        super(entity);
    }

    @Override
    public boolean canContinueToUse() {
        final boolean cont = super.canContinueToUse();
        if (cont) {
            this.tick(); //we only fire once
        }
        return cont;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.shouldDoOnDoneOnce) {
            this.shouldDoOnDoneOnce = false;
            try {
                for (Runnable done : this.onDoneOnce) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), done);
                }
            } catch (IllegalPluginAccessException ignored) {
            }
        }
    }

    public void addOnceOnDone(Runnable onDone) {
        this.onDoneOnce.add(onDone);
    }
}
