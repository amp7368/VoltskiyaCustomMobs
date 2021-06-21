package apple.voltskiya.custom_mobs.pathfinders;

import apple.voltskiya.custom_mobs.reload.PluginDisable;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBowShoot;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.monster.IRangedEntity;

public class PathfinderGoalBowShootNoBow<T extends EntityMonster & IRangedEntity> extends PathfinderGoalBowShoot<T> {
    public PathfinderGoalBowShootNoBow(T var0, double var1, int var3, float var4) {
        super(var0, var1, var3, var4);
        PluginDisable.addMob(var0,this);
    }

    @Override
    protected boolean g() {
        return true; // I HAVE A BOW I SWEAR
    }
}
