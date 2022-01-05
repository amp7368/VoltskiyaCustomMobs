package apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.bowlike;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.pathfinders.PathfinderGoalBowShootNoBow;
import apple.voltskiya.mob_manager.parent.listen.MMSpawnListener;
import apple.voltskiya.mob_manager.parent.listen.SpawnHandlerListener;
import apple.voltskiya.mob_manager.parent.mob.MMSpawned;
import net.minecraft.world.entity.monster.EntitySkeleton;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class BowlikeMoveManager implements SpawnHandlerListener {
    public BowlikeMoveManager() {
        MMSpawnListener.get().addListener(this);
    }

    @Override
    public void handle(MMSpawned mmSpawned) {
        DecodeEntity.getGoalSelector(mmSpawned.getNmsMob()).a(4, new PathfinderGoalBowShootNoBow<>((EntitySkeleton) mmSpawned.getNmsMob(), 1.0D, 20, 15.0F));
    }

    @Override
    public boolean shouldHandle(CreatureSpawnEvent event) {
        return event.getEntity() instanceof Skeleton;
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
