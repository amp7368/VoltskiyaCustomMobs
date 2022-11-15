package apple.voltskiya.custom_mobs.abilities.nether.fire_fangs;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class FireFangLine {
    private final Vector direction;
    private final Location location;
    private int ticksToLive;
    private final int fireLength;

    public FireFangLine(Vector direction, Location location, int ticksToLive, int fireLength) {
        this.direction = direction;
        this.location = location;
        this.ticksToLive = ticksToLive;
        this.fireLength = fireLength;
    }

    public Vector getDirection() {
        return direction;
    }

    public Location getLocation() {
        return location;
    }

    public int getTicksToLive() {
        return ticksToLive;
    }

    public int getFireLength() {
        return fireLength;
    }

    public void decrementLife() {
        ticksToLive--;
    }

    public boolean isDead() {
        return ticksToLive <= 0;
    }

    public void die() {
        this.ticksToLive = 0;
    }
}
