package apple.voltskiya.custom_mobs.heartbeat.tick.orbital_strike;

import apple.voltskiya.custom_mobs.DistanceUtils;
import apple.voltskiya.custom_mobs.heartbeat.tick.SpawnEater;
import apple.voltskiya.custom_mobs.heartbeat.tick.main.*;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.*;
import java.util.List;

public class OrbitalStrikeManagerTicker implements SpawnEater {
    public static final double STRIKE_CHANCE = 0.01;
    public static final long STRIKE_COOLDOWN = 1000 / 20 * 300;
    public static final double STRIKE_DISTANCE = 100;

    public static final double STRIKE_TARGET_RADIUS = 6;
    public static final double STRIKE_TARGET_TOWER_HEIGHT = 20;
    public static final int STRIKE_TIME = 300;
    public static final int STRIKE_TARGET_TIME = 30;
    public static final double DESTRUCTION_BLAZE_INTERVAL = 1;

    private static OrbitalStrikeManagerTicker instance;
    private final Map<Closeness, OrbitalStrikeIndividualTicker> closenessToStrikeres = new HashMap<>() {{
        for (Closeness closeness : Closeness.values())
            put(closeness, new OrbitalStrikeIndividualTicker(closeness));
    }};

    public OrbitalStrikeManagerTicker() {
        instance = this;
        closenessToStrikeres.get(Closeness.HIGH_CLOSE).setIsCheckStrike();
    }

    public static OrbitalStrikeManagerTicker get() {
        return instance;
    }

    @Override
    public void eatEvent(CreatureSpawnEvent event) {
        System.out.println("eat");
        // this is a striker
        final Entity striker = event.getEntity();
        Closeness closeness = determineConcern(striker);
        closenessToStrikeres.get(closeness).giveStriker(striker, 0);
    }

    public boolean amIGivingStriker(Entity entity, Closeness currentCloseness, long lastStrike) {
        Closeness actualCloseness = determineConcern(entity);
        if (actualCloseness != currentCloseness) {
            closenessToStrikeres.get(actualCloseness).giveStriker(entity, lastStrike);
            return true;
        }
        return false;
    }

    private Closeness determineConcern(Entity striker) {
        Location strikerLocation = striker.getLocation();

        List<Player> players = UpdatedPlayerList.getPlayers();
        for (Player player : players) {
            Location playerLocation = player.getLocation();
            return Closeness.getCloseness(strikerLocation, playerLocation);
        }
        return Closeness.lowest();
    }

    enum Closeness {
        HIGH_CLOSE(100, LowFrequencyTick.get()),
        NORMAL_CLOSE(200, VeryLowFrequencyTick.get());

        private final double distance;
        private static final Closeness[] order = new Closeness[]{HIGH_CLOSE, NORMAL_CLOSE};
        private final TickGiverable giver;

        Closeness(double distance, TickGiverable giver) {
            this.distance = distance;
            this.giver = giver;
        }

        private static Closeness getCloseness(Location aLocation, Location bLocation) {
            double d = DistanceUtils.distance(aLocation, bLocation);
            for (Closeness closeness : order) {
                if (closeness.distance >= d) {
                    return closeness;
                }
            }
            return lowest();
        }

        public static Closeness lowest() {
            return order[order.length - 1];
        }

        public TickGiverable getGiver() {
            return giver;
        }
    }
}
