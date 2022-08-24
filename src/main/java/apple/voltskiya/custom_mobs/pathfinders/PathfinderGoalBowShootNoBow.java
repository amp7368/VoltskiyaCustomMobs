package apple.voltskiya.custom_mobs.pathfinders;

import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;

public class PathfinderGoalBowShootNoBow<T extends Monster & RangedAttackMob> extends
    RangedBowAttackGoal<T> {

    public PathfinderGoalBowShootNoBow(T var0, double var1, int var3, float var4) {
        super(var0, var1, var3, var4);
    }

    @Override
    protected boolean isHoldingBow() {
        return true; // I HAVE A BOW I SWEAR
    }
}
