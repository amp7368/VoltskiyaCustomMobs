package apple.voltskiya.custom_mobs.dungeon.product;

import org.bukkit.Location;

public class DungeonPlayerIO {
    private Location pos1 = null;
    private Location pos2 = null;
    private Location center;

    public void pos1(Location location) {
        this.pos1 = location;
    }

    public void pos2(Location location) {
        this.pos2 = location;
    }

    public void center(Location center) {
        this.center = center;
    }


    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public Location getCenter() {
        return center;
    }
}
