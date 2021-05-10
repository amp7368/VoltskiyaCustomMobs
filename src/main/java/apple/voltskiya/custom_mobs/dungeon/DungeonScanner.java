package apple.voltskiya.custom_mobs.dungeon;

import apple.voltskiya.custom_mobs.dungeon.gui.DungeonGui;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DungeonScanner {
    private Location pos1 = null;
    private Location pos2 = null;

    public void pos1(Location location) {
        this.pos1 = location;
    }

    public void pos2(Location location) {
        this.pos2 = location;
    }

    public void scan() {

    }

    public void gui(Player player) {
        new DungeonGui(player, this);
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }
}
