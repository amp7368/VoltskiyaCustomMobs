package apple.voltskiya.custom_mobs.dungeon.patrols;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class PatrolStep {
    private final Location location;

    public PatrolStep(Block clickedBlock) {
        this.location = clickedBlock.getLocation();
    }

    public Location getLocation() {
        return location;
    }
}
