package apple.voltskiya.custom_mobs.mobs.pathfinders;

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
    }

    @Override
    protected void a(EntityInsentient entityinsentient, EntityLiving entityliving) {
        if (confirmTarget.test(entityliving)) super.a(entityinsentient, entityliving);
    }

    @Override
    public void c() {
        if (confirmTarget.test(this.e.getLastDamager())) super.c();
    }
}
