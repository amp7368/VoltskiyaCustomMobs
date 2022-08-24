package apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.bowlike;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.pathfinders.PathfinderGoalBowShootNoBow;
import apple.voltskiya.mob_manager.listen.MMSpawnListener;
import apple.voltskiya.mob_manager.listen.SpawnHandlerListener;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import net.minecraft.world.entity.monster.Skeleton;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class BowlikeMoveManager implements SpawnHandlerListener {

    public BowlikeMoveManager() {
        MMSpawnListener.get().addListener(this);
    }

    @Override
    public void handle(MMSpawned mmSpawned) {
        Skeleton nmsMob = (Skeleton) mmSpawned.getNmsMob();
        PathfinderGoalBowShootNoBow<Skeleton> pathfinder = new PathfinderGoalBowShootNoBow<>(
            nmsMob,
            1.0D,
            20,
            15.0F
        );
        DecodeEntity.getGoalSelector(mmSpawned.getNmsMob()).addGoal(4, pathfinder);
    }

    @Override
    public boolean shouldHandle(CreatureSpawnEvent event) {
        return event.getEntity() instanceof org.bukkit.entity.Skeleton;
    }

    @Override
    public String getTag() {
        return "bow_like_move";
    }

    @Override
    public String getName() {
        return "bow_like_move";
    }
}
