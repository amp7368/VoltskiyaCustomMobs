package apple.voltskiya.custom_mobs.pathfinders.utilities;

import apple.voltskiya.custom_mobs.mobs.nms.parent.utility.NmsUtilityWrapper;
import apple.voltskiya.custom_mobs.reload.PluginDisable;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;

import java.util.function.Predicate;

public class HurtByTargetGoalExcept extends HurtByTargetGoal {
    private final Predicate<EntityLiving> confirmTarget;
    private final NmsUtilityWrapper<PathfinderMob> wrapper;

    public HurtByTargetGoalExcept(PathfinderMob entitycreature, Predicate<EntityLiving> confirmTarget, Class<?>... aclass) {
        super(entitycreature, aclass);
        this.wrapper = new NmsUtilityWrapper<>(entitycreature);
        this.confirmTarget = confirmTarget;
        PluginDisable.addMob(entitycreature, this);
    }

    /**
     * set the target
     *
     * @param entityinsentient me
     * @param entityliving     them
     */
    @Override
    protected void a(Mob entityinsentient, EntityLiving entityliving) {
        if (confirmTarget.test(entityliving)) super.a(entityinsentient, entityliving);
    }

    /**
     * @return true if we should keep running. otherwise false
     */
    @Override
    public boolean b() {
        return confirmTarget.test(this.wrapper.getLastDamager());
    }
}
