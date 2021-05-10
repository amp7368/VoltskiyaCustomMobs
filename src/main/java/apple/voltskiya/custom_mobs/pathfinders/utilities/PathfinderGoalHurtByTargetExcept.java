package apple.voltskiya.custom_mobs.pathfinders.utilities;

import apple.voltskiya.custom_mobs.reload.PluginDisable;
import net.minecraft.server.v1_16_R3.EntityCreature;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.PathfinderGoalHurtByTarget;

import java.util.function.Predicate;

public class PathfinderGoalHurtByTargetExcept extends PathfinderGoalHurtByTarget {
    private final Predicate<EntityLiving> confirmTarget;

    public PathfinderGoalHurtByTargetExcept(EntityCreature entitycreature, Predicate<EntityLiving> confirmTarget, Class<?>... aclass) {
        super(entitycreature, aclass);
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
    protected void a(EntityInsentient entityinsentient, EntityLiving entityliving) {
        if (confirmTarget.test(entityliving)) super.a(entityinsentient, entityliving);
    }

    /**
     * @return true if we should keep running. otherwise false
     */
    @Override
    public boolean b() {
        return (confirmTarget.test(this.e.getLastDamager()));
    }
}
