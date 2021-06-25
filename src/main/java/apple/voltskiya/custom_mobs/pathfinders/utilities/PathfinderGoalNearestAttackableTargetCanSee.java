package apple.voltskiya.custom_mobs.pathfinders.utilities;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

public class PathfinderGoalNearestAttackableTargetCanSee<T extends EntityLiving> extends PathfinderGoalNearestAttackableTarget<T> {
    private static final double DEFAULT_RADIANS = Math.toRadians(65);
    private double fieldOfViewRadians = DEFAULT_RADIANS;
    private final Collection<Runnable> onDoneOnce = new ArrayList<>();
    private boolean doLineOfSightPredicate = true;
    private boolean shouldDoOnDoneOnce = true;

    public PathfinderGoalNearestAttackableTargetCanSee(EntityInsentient entityinsentient, Class<T> oclass, boolean flag, double fieldOfViewDegrees) {
        super(entityinsentient, oclass, flag);
        this.fieldOfViewRadians = Math.toRadians(fieldOfViewDegrees);
        this.lineOfSightCondition();
    }

    public PathfinderGoalNearestAttackableTargetCanSee(EntityInsentient entityinsentient, Class<T> oclass, boolean flag, boolean flag1, double fieldOfViewDegrees) {
        super(entityinsentient, oclass, flag, flag1);
        this.fieldOfViewRadians = Math.toRadians(fieldOfViewDegrees);
        this.lineOfSightCondition();
    }

    public PathfinderGoalNearestAttackableTargetCanSee(EntityInsentient entityinsentient, Class<T> oclass, int i, boolean flag, boolean flag1, @Nullable Predicate<EntityLiving> predicate, double fieldOfViewDegrees) {
        super(entityinsentient, oclass, i, flag, flag1, predicate);
        this.fieldOfViewRadians = Math.toRadians(fieldOfViewDegrees);
        this.lineOfSightCondition();
    }

    public PathfinderGoalNearestAttackableTargetCanSee(EntityInsentient entityinsentient, Class<T> oclass, boolean flag) {
        super(entityinsentient, oclass, flag);
        this.lineOfSightCondition();
    }

    public PathfinderGoalNearestAttackableTargetCanSee(EntityInsentient entityinsentient, Class<T> oclass, boolean flag, boolean flag1) {
        super(entityinsentient, oclass, flag, flag1);
        this.lineOfSightCondition();
    }

    public PathfinderGoalNearestAttackableTargetCanSee(EntityInsentient entityinsentient, Class<T> oclass, int i, boolean flag, boolean flag1, @Nullable Predicate<EntityLiving> predicate) {
        super(entityinsentient, oclass, i, flag, flag1, predicate);
        this.lineOfSightCondition();
    }

    private void lineOfSightCondition() {
        if (doLineOfSightPredicate) {
            doLineOfSightPredicate = false;
            this.d.a(this::canSee);
        }
    }

    private boolean canSee(EntityLiving entityLiving) {
        Vec3D lookDirectionNMS = this.e.getLookDirection(); // me
        Vector lookDirection = new Vector(lookDirectionNMS.getX(), lookDirectionNMS.getY(), lookDirectionNMS.getZ());
        Vector realDirection = new Vector(entityLiving.locX() - this.e.locX(), entityLiving.locY() - this.e.locY(), entityLiving.locZ() - this.e.locZ());
        return lookDirection.angle(realDirection) <= this.fieldOfViewRadians;
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
