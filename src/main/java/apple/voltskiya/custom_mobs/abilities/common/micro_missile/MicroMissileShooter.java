package apple.voltskiya.custom_mobs.abilities.common.micro_missile;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.pathfinders.spell.PathfinderGoalShootMicroMissle;
import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import net.minecraft.world.entity.Mob;

public class MicroMissileShooter implements SpawnListener {

    private static final int COOLDOWN = 20 * 10;

    public MicroMissileShooter() {
        this.registerSpawnListener();
    }

    @Override
    public boolean isOnlyMobs() {
        return true;
    }

    @Override
    public void doSpawn(MMSpawned mmSpawned) {
        Mob mob = mmSpawned.getNmsMob();
        DecodeEntity.getGoalSelector(mob)
            .addGoal(0, new PathfinderGoalShootMicroMissle(mob, 2 * 20, 1, MissileType.LONER));
        DecodeEntity.getGoalSelector(mob)
            .addGoal(1, new PathfinderGoalShootMicroMissle(mob, COOLDOWN, 5, MissileType.FLURRY));
    }

    /**
     * @return the name of the sub_module (a step below a module)
     */
    @Override
    public String getBriefTag() {
        return "micro_missiles";
    }

    public enum MissileType {
        FLURRY(MicroMissileConfig.get().speed, MicroMissileConfig.get().minTicksToLive,
            MicroMissileConfig.get().damageAmount),
        LONER(MicroMissileConfig.get().speed * 1.25, MicroMissileConfig.get().minTicksToLive * 3,
            MicroMissileConfig.get().damageAmount * 2);
        public final double damageAmount;
        public final int minTicksToLive;
        public final double speed;

        MissileType(double speed, int minTicksToLive, double damageAmount) {
            this.minTicksToLive = minTicksToLive;
            this.speed = speed;
            this.damageAmount = damageAmount;
        }
    }
}
