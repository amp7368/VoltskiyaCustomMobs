package apple.voltskiya.custom_mobs.ai.bowlike;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.pathfinders.PathfinderGoalBowShootNoBow;
import apple.voltskiya.mob_manager.listen.MMSpawnListener;
import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class BowlikeMoveManager implements SpawnListener {

    public BowlikeMoveManager() {
        MMSpawnListener.get().addListener(this);
    }

    @Override
    public void doSpawn(MMSpawned mmSpawned) {
        Mob nms = mmSpawned.getNmsMob();
        PathfinderGoalBowShootNoBow<?> pathfinder = new PathfinderGoalBowShootNoBow<>(
            (Monster & RangedAttackMob) nms, 1.0D, 20, 15.0F);
        DecodeEntity.getGoalSelector(nms).addGoal(4, pathfinder);
    }

    @Override
    public boolean shouldHandle(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        return entity instanceof Monster && entity instanceof RangedAttackMob;
    }

    @Override
    public String getBriefTag() {
        return "bow_like_move";
    }
}
