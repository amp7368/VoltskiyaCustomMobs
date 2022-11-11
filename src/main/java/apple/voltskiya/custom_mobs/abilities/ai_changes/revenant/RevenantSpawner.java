package apple.voltskiya.custom_mobs.abilities.ai_changes.revenant;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.pathfinders.PathfinderGoalBowShootNoBow;
import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Skeleton;

public class RevenantSpawner extends NmsSpawner<Skeleton> implements SpawnListener {

    public RevenantSpawner() {
        super("revenant");
        this.registerSpawnListener();
    }

    @Override
    public void doSpawn(MMSpawned mmSpawned) {
        DecodeEntity.getGoalSelector(mmSpawned.getNmsMob()).addGoal(4,
            new PathfinderGoalBowShootNoBow<>((Monster & RangedAttackMob) mmSpawned.getNmsMob(),
                1.0D, 20, 15.0F));
    }


    @Override
    protected EntityFactory<Skeleton> getEntityFactory() {
        return NmsRevenant::new;
    }

    @Override
    public EntityType<Skeleton> getEntityType() {
        return EntityType.SKELETON;
    }
}
