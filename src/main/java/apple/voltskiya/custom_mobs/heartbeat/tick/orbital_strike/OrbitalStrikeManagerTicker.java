package apple.voltskiya.custom_mobs.heartbeat.tick.orbital_strike;

import apple.voltskiya.custom_mobs.DistanceUtils;
import apple.voltskiya.custom_mobs.heartbeat.tick.SpawnEater;
import apple.voltskiya.custom_mobs.heartbeat.tick.main.*;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.io.IOException;
import java.util.*;
import java.util.List;

public class OrbitalStrikeManagerTicker extends SpawnEater {
    public final double STRIKE_CHANCE;
    public final long STRIKE_COOLDOWN;
    public final double STRIKE_DISTANCE;

    public final double STRIKE_TARGET_RADIUS;
    public final double STRIKE_HEIGHT;
    public final int STRIKE_TIME;
    public final int STRIKE_TARGET_TIME;
    public final double DESTRUCTION_BLAZE_INTERVAL;

    private static OrbitalStrikeManagerTicker instance;
    private final Map<Closeness, OrbitalStrikeIndividualTicker> closenessToStrikeres = new HashMap<>() {{
        for (Closeness closeness : Closeness.values())
            put(closeness, new OrbitalStrikeIndividualTicker(closeness));
    }};

    public OrbitalStrikeManagerTicker() throws IOException {
        this.STRIKE_CHANCE = (double) getValueOrInit(YmlSettings.STRIKE_CHANCE.getPath());
        this.STRIKE_COOLDOWN =  (1000L / 20 * (int) getValueOrInit(YmlSettings.STRIKE_COOLDOWN.getPath()));
        this.STRIKE_DISTANCE = (int) getValueOrInit(YmlSettings.STRIKE_DISTANCE.getPath());
        this.STRIKE_TARGET_RADIUS = (double) getValueOrInit(YmlSettings.STRIKE_TARGET_RADIUS.getPath());
        this.STRIKE_HEIGHT = (int) getValueOrInit(YmlSettings.STRIKE_HEIGHT.getPath());
        this.STRIKE_TIME = (int) getValueOrInit(YmlSettings.STRIKE_TIME.getPath());
        this.STRIKE_TARGET_TIME = (int) getValueOrInit(YmlSettings.STRIKE_TARGET_TIME.getPath());
        this.DESTRUCTION_BLAZE_INTERVAL = (double) getValueOrInit(YmlSettings.DESTRUCTION_BLAZE_INTERVAL.getPath());
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

    @Override
    public String getName() {
        return "orbital_strike";
    }

    @Override
    public void initializeYml() throws IOException {
        for (YmlSettings setting : YmlSettings.values()) {
            setValueIfNotExists(setting.getPath(), setting.value);
        }
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

    private enum YmlSettings {
        STRIKE_CHANCE("chancePerTick", 0.01d),
        STRIKE_COOLDOWN("cooldown", 300),
        STRIKE_DISTANCE("targetingRange", 100),
        STRIKE_TARGET_RADIUS("radius", 6d),
        STRIKE_HEIGHT("height", 20),
        STRIKE_TIME("totalTime", 300),
        STRIKE_TARGET_TIME("targetTime", 30),
        DESTRUCTION_BLAZE_INTERVAL("shootInterval", 1d);

        private final String path;
        private final Object value;

        YmlSettings(String path, Object value) {
            this.path = path;
            this.value = value;
        }

        public String getPath() {
            return path;
        }

        public Object getValue() {
            return value;
        }
    }
}
