package apple.voltskiya.custom_mobs.pathfinders.utilities;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import org.bukkit.Bukkit;
import org.bukkit.plugin.IllegalPluginAccessException;

import java.util.ArrayList;
import java.util.Collection;

public class PathfinderGoalHurtByTargetOnDone extends PathfinderGoalHurtByTarget {
    private final Collection<Runnable> onDoneOnce = new ArrayList<>();
    private boolean shouldDoOnDoneOnce = true;

    public PathfinderGoalHurtByTargetOnDone(EntityCreature entitycreature, Class<?>... aclass) {
        super(entitycreature, aclass);
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
