package apple.voltskiya.custom_mobs.dungeon.product;

import apple.voltskiya.custom_mobs.dungeon.scanned.DungeonChestScanned;
import apple.voltskiya.custom_mobs.dungeon.scanned.DungeonMobScanned;
import apple.voltskiya.custom_mobs.dungeon.scanned.DungeonScanned;
import apple.voltskiya.custom_mobs.dungeon.scanner.DungeonScanner;
import org.bukkit.Location;

import java.util.List;

public class Dungeon {
    public Dungeon(DungeonScanner dungeonScanner, DungeonScanned dungeonScanned, Location center, String dungeonName) {
        List<DungeonChestScanned> chests = dungeonScanned.getChests();
        List<DungeonMobScanned> mobs = dungeonScanned.getMobs();

    }
}
