package apple.voltskiya.custom_mobs.pathfinders.utilities;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class PathfinderGoalNearestAttackableTargetCanSee<T extends EntityLiving> extends PathfinderGoalNearestAttackableTarget<T> {
    private static final double DEFAULT_RADIANS = Math.toRadians(65);
    private final EntityInsentient me;
    private final double fieldOfViewRadians = DEFAULT_RADIANS;
    private final Collection<Runnable> onDoneOnce = new ArrayList<>();
    private boolean doLineOfSightPredicate = true;
    private boolean shouldDoOnDoneOnce = true;

    public PathfinderGoalNearestAttackableTargetCanSee(EntityInsentient entityinsentient, Class<T> oclass, boolean flag) {
        super(entityinsentient, oclass, flag);
        this.lineOfSightCondition();
        me = entityinsentient;
    }


    private void lineOfSightCondition() {
        if (doLineOfSightPredicate) {
            doLineOfSightPredicate = false;
            this.d.a(this::canSee);
        }
    }

    private boolean canSee(EntityLiving entityLiving) {
        Location myLocation = this.me.getBukkitEntity().getLocation();
        @NotNull Vector lookDirectionNMS = myLocation.getDirection(); // me
        Vector lookDirection = new Vector(lookDirectionNMS.getX(), lookDirectionNMS.getY(), lookDirectionNMS.getZ());
        Location themLocation = entityLiving.getBukkitEntity().getLocation();

        Vector realDirection = themLocation.subtract(myLocation).toVector();
        return lookDirection.angle(realDirection) <= this.fieldOfViewRadians;
    }

    @Override
    public boolean b() {
        if (super.b()) {
            this.d(); //we only fire once
        }
        return false;
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
