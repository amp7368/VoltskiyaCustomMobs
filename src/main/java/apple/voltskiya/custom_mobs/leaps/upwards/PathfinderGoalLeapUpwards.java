package apple.voltskiya.custom_mobs.leaps.upwards;

import apple.voltskiya.custom_mobs.leaps.LeapType;
import apple.voltskiya.custom_mobs.leaps.misc.PathfinderGoalLeap;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityLiving;

import java.util.function.BooleanSupplier;

public class PathfinderGoalLeapUpwards extends PathfinderGoalLeap {

    public static final int PEAK_MARGIN_OF_ERROR = 2;

    /**
     * find a block to navigate to
     *
     * @param me                    the entity to navigate
     * @param type                  the type of leap I am
     * @param shouldStopCurrentLeap a function that will say if the mob should stop the leap at a random point
     *                              in the jump (ie. if the mob is hit)
     * @param isOnGround            a function to say whether the mob is on the ground or not
     */
    public PathfinderGoalLeapUpwards(EntityInsentient me, LeapType type, BooleanSupplier shouldStopCurrentLeap, BooleanSupplier isOnGround) {
        super(me, type.getLeapConfig(), shouldStopCurrentLeap, isOnGround);
    }

    /**
     * @return round 2 of whether I want to run ?
     */
    @Override
    public boolean b() {
        // navigationAbstract.m() returns true if the entity is *not* navigating anywhere
        final EntityLiving goalTarget = this.me.getGoalTarget();
        if (goalTarget == null) return false;
        // return what above me said and make sure that the mob is higher than a few blocks below the peak of where I'm going
        return super.b() && this.me.locY() - goalTarget.locY() > this.config.getPeak() - PEAK_MARGIN_OF_ERROR;
    }
}
