package apple.voltskiya.custom_mobs.dungeon.product;

import java.util.HashMap;

public class DungeonActive {
    private static final HashMap<String, Dungeon> dungeons = new HashMap<>();

    public static synchronized Dungeon getDungeon(String name) {
        return dungeons.compute(name, (n, d) -> d == null ? new Dungeon(n) : d);
    }
}
