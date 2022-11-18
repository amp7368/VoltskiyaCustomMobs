package apple.voltskiya.custom_mobs.abilities.nether.fire_fangs;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.pathfinders.spell.PathfinderGoalShootSpell;
import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.entity.Mob;

public class FireFangsManager implements SpawnListener {

    public Map<String, FireFangsType> tagToFangType;

    public FireFangsManager() {
        tagToFangType = new HashMap<>() {{
            put("fire_fangs_basic", FireFangsType.NORMAL);
            put("fire_fangs_triple", FireFangsType.TRIPLE);
            put("fire_fangs_triple_straight", FireFangsType.TRIPLE_STRAIGHT);
            put("fire_fangs_basic_blue", FireFangsType.BLUE_NORMAL);
            put("fire_fangs_triple_blue", FireFangsType.BLUE_TRIPLE);
            put("fire_fangs_triple_blue_straight", FireFangsType.BLUE_TRIPLE_STRAIGHT);
        }};
        this.registerSpawnListener();
    }

    @Override
    public String getBriefTag() {
        return "fire_fangs";
    }


    @Override
    public void doSpawn(MMSpawned mmSpawned) {
        Mob entity = (Mob) mmSpawned.getNmsEntity();
        for (String tag : mmSpawned.getEntity().getScoreboardTags()) {
            FireFangsType type = tagToFangType.get(tag);
            if (type == null)
                continue;
            DecodeEntity.getGoalSelector(entity)
                .addGoal(0, new PathfinderGoalShootSpell<>(new FireFangsCaster(entity), type));
        }
    }

}
