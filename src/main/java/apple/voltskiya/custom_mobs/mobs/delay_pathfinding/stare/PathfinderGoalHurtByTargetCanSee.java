package apple.voltskiya.custom_mobs.mobs.delay_pathfinding.stare;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import org.bukkit.Bukkit;
import org.bukkit.plugin.IllegalPluginAccessException;

import java.util.ArrayList;
import java.util.Collection;

public class PathfinderGoalHurtByTargetCanSee extends PathfinderGoalHurtByTarget {
    private static final double DEFAULT_RADIANS = Math.toRadians(65);
    private final double fieldOfViewRadians = DEFAULT_RADIANS;
    private final Collection<Runnable> onDoneOnce = new ArrayList<>();
    private final boolean doLineOfSightPredicate = true;
    private boolean shouldDoOnDoneOnce = true;

    public PathfinderGoalHurtByTargetCanSee(EntityCreature entity) {
        super(entity);
    }

    @Override
    public boolean b() {
        final boolean b = super.b();
        if (b) {
            this.d(); //we only fire once
        }
        return b;
    }

    @Override
    public void d() {
        super.d();
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
