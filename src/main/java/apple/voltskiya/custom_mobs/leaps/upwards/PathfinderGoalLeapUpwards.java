package apple.voltskiya.custom_mobs.leaps.upwards;

import apple.voltskiya.custom_mobs.leaps.LeapType;
import apple.voltskiya.custom_mobs.leaps.PathfinderGoalLeap;
import apple.voltskiya.custom_mobs.leaps.config.LeapPostConfig;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityLiving;


public class PathfinderGoalLeapUpwards extends PathfinderGoalLeap {

    public static final int PEAK_MARGIN_OF_ERROR = 3;

    /**
     * find a block to navigate to
     *
     * @param me         the entity to navigate
     * @param type       the type of leap I am
     * @param postConfig provides any runtime info for the leap
     */
    public PathfinderGoalLeapUpwards(EntityInsentient me, LeapType type, LeapPostConfig postConfig) {
        super(me, type.getLeapConfig(), postConfig);
    }

    /**
     * @return whether I want to run
     */
    @Override
    public boolean a() {
        // navigationAbstract.m() returns true if the entity is *not* navigating anywhere
        final EntityLiving goalTarget = this.me.getGoalTarget();
        if (goalTarget == null) return false;
        // return what above me said and make sure that the mob is higher than a few blocks below the peak of where I'm going
        return super.a() && goalTarget.locY() - this.me.locY() > this.config.getPeak() - PEAK_MARGIN_OF_ERROR;
    }
}
