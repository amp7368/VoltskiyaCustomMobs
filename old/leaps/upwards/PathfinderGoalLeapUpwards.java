package apple.voltskiya.custom_mobs.leaps.upwards;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.leaps.GoalLeap;
import apple.voltskiya.custom_mobs.leaps.config.LeapPostConfig;
import apple.voltskiya.custom_mobs.leaps.config.LeapPreConfig;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityLiving;


public class GoalLeapUpwards extends GoalLeap {

    public static final int PEAK_MARGIN_OF_ERROR = 3;

    /**
     * find a block to navigate to
     *
     * @param me         the entity to navigate
     * @param type       the type of leap I am
     * @param postConfig provides any runtime info for the leap
     */
    public GoalLeapUpwards(Mob me, LeapPreConfig type, LeapPostConfig postConfig) {
        super(me, type, postConfig);
    }

    /**
     * @return whether I want to run
     */
    @Override
    public boolean a() {
        // navigationAbstract.m() returns true if the entity is *not* navigating anywhere
        final EntityLiving goalTarget = DecodeEntity.getLastTarget(this.me);
        if (goalTarget == null) return false;
        // return what above me said and make sure that the mob is higher than a few blocks below the peak of where I'm going
        return super.a() && goalTarget.getBukkitEntity().getLocation().getY() - this.me.getBukkitEntity().getLocation().getY() > this.config.getPeak() - PEAK_MARGIN_OF_ERROR;
    }
}
