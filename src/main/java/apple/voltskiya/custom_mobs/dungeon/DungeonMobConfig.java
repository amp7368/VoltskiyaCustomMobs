package apple.voltskiya.custom_mobs.dungeon;

import java.util.ArrayList;
import java.util.List;

/**
 * a configuration for a type of spawn
 * the material to represent
 */
public class DungeonMobConfig {
    public static final String PREFIX_TAG = "dungeon.config.";
    public String nameToRepresentMob;
    public List<DungeonMobInfo> mobs = new ArrayList<>();

    public DungeonMobConfig(String configName) {
        this.nameToRepresentMob = configName;
    }

    public void add(DungeonMobInfo mob) {
        this.mobs.add(mob);
    }

    public String getName() {
        return nameToRepresentMob;
    }
}
