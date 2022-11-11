package apple.voltskiya.custom_mobs.abilities.ai_changes.micro_misles;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.pathfinders.spell.PathfinderGoalShootMicroMissle;
import apple.voltskiya.mob_manager.listen.MMSpawnListener;
import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import net.minecraft.world.entity.Mob;

public class MicroMissleShooter implements SpawnListener {

    private static final int COOLDOWN = 20 * 10;

    public MicroMissleShooter() {
        MMSpawnListener.get().addListener(this);
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
        return "micro_missile_shooter";
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
