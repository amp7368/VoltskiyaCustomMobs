package apple.voltskiya.custom_mobs.pathfinders.utilities;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class GoalCanSeeTarget<T extends LivingEntity> extends
    NearestAttackableTargetGoal<T> {

    private static final double DEFAULT_FOV = Math.toRadians(65);
    private final List<Runnable> callbacks = new ArrayList<>(1);
    private final org.bukkit.entity.Mob bukkitMob;
    private LivingEntity setTarget;

    public GoalCanSeeTarget(Mob mob, Class<T> otherClass) {
        super(mob, otherClass, true);
        this.bukkitMob = (org.bukkit.entity.Mob) mob.getBukkitEntity();
        this.targetConditions.selector(this::canSee);
    }


    private boolean canSee(LivingEntity them) {
        Location myLocation = bukkitMob.getEyeLocation();
        Location themLocation = ((org.bukkit.entity.LivingEntity) them.getBukkitEntity()).getEyeLocation();

        Vector realDirection = themLocation.clone().subtract(myLocation).toVector();
        Vector lookDirection = myLocation.getDirection();
        float angle = lookDirection.angle(realDirection);
        if (angle > DEFAULT_FOV) return false;

        boolean canSee = bukkitMob.hasLineOfSight(themLocation);
        if (canSee) this.setTarget = them;
        return canSee;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void stop() {
        this.setTarget(setTarget);
        for (Runnable done : this.callbacks) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), done);
        }
    }

    public void addCallback(Runnable onDone) {
        this.callbacks.add(onDone);
    }
}
