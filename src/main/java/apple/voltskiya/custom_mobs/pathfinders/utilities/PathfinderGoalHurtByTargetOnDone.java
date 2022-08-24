package apple.voltskiya.custom_mobs.pathfinders.utilities;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import org.bukkit.Bukkit;
import org.bukkit.plugin.IllegalPluginAccessException;

public class PathfinderGoalHurtByTargetOnDone extends HurtByTargetGoal {

    private final Collection<Runnable> onDoneOnce = new ArrayList<>();
    private boolean shouldDoOnDoneOnce = true;

    public PathfinderGoalHurtByTargetOnDone(PathfinderMob entitycreature, Class<?>... aclass) {
        super(entitycreature, aclass);
    }

    @Override
    public boolean canContinueToUse() {
        final boolean b = super.canContinueToUse();
        if (b) {
            this.tick(); //we only fire once
        }
        return b;
    }

    @Override
    public void tick() {
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
