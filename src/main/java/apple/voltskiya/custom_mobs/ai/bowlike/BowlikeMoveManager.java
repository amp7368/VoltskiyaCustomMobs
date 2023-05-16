package apple.voltskiya.custom_mobs.ai.bowlike;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.ai.AiModule;
import apple.voltskiya.custom_mobs.pathfinders.PathfinderGoalBowShootNoBow;
import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;

public class BowlikeMoveManager implements SpawnListener {

    public BowlikeMoveManager() {
        this.registerSpawnListener();
    }

    @Override
    public void doSpawn(MMSpawned mmSpawned) {
        Mob nms = mmSpawned.getNmsMob();
        PathfinderGoalBowShootNoBow<?> pathfinder = new PathfinderGoalBowShootNoBow<>(
            (Monster & RangedAttackMob) nms, 1.0D, 20, 15.0F);
        DecodeEntity.getGoalSelector(nms).addGoal(0, pathfinder);
    }

    @Override
    public boolean shouldHandle(Entity entity) {
        return ((CraftEntity) entity).getHandle() instanceof Monster monster
            && monster instanceof RangedAttackMob;
    }

    @Override
    public String getExtensionTag() {
        return AiModule.EXTENSION_TAG;
    }

    @Override
    public String getBriefTag() {
        return "bow_like_move";
    }
}
