package apple.voltskiya.custom_mobs.tick.orbital_strike.large;

import apple.voltskiya.custom_mobs.main.ticking.LowFrequencyTick;
import apple.voltskiya.custom_mobs.main.ticking.VeryLowFrequencyTick;
import apple.voltskiya.custom_mobs.util.DistanceUtils;
import apple.voltskiya.custom_mobs.tick.MobListSql;
import apple.voltskiya.custom_mobs.tick.SpawnEater;
import apple.voltskiya.custom_mobs.main.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;

public class LargeOrbitalStrikeManagerTicker extends SpawnEater {
    public final double STRIKE_MOVEMENT_SPEED;
    public final double STRIKE_MOVEMENT_LAG;
    public final double STRIKE_CHANCE;
    public final long STRIKE_COOLDOWN;
    public final double STRIKE_DISTANCE;

    public final double STRIKE_TARGET_RADIUS;
    public final double STRIKE_HEIGHT;
    public final int STRIKE_TIME;
    public final int STRIKE_TARGET_TIME;
    public final double DESTRUCTION_BLAZE_INTERVAL;

    private static LargeOrbitalStrikeManagerTicker instance;
    private final Map<Closeness, LargeOrbitalStrikeIndividualTicker> closenessToStrikeres = new HashMap<>() {{
        for (Closeness closeness : Closeness.values())
            put(closeness, new LargeOrbitalStrikeIndividualTicker(closeness));
    }};
    private final long callerUid = UpdatedPlayerList.callerUid();

    public LargeOrbitalStrikeManagerTicker() throws IOException {
        this.STRIKE_CHANCE = (double) getValueOrInit("large", YmlSettings.STRIKE_CHANCE.getPath());
        this.STRIKE_COOLDOWN = (1000L / 20 * (int) getValueOrInit("large", YmlSettings.STRIKE_COOLDOWN.getPath()));
        this.STRIKE_DISTANCE = (int) getValueOrInit("large", YmlSettings.STRIKE_DISTANCE.getPath());
        this.STRIKE_TARGET_RADIUS = (double) getValueOrInit("large", YmlSettings.STRIKE_TARGET_RADIUS.getPath());
        this.STRIKE_HEIGHT = (int) getValueOrInit("large", YmlSettings.STRIKE_HEIGHT.getPath());
        this.STRIKE_TIME = (int) getValueOrInit("large", YmlSettings.STRIKE_TIME.getPath());
        this.STRIKE_TARGET_TIME = (int) getValueOrInit("large", YmlSettings.STRIKE_TARGET_TIME.getPath());
        this.DESTRUCTION_BLAZE_INTERVAL = (double) getValueOrInit("large", YmlSettings.DESTRUCTION_BLAZE_INTERVAL.getPath());
        this.STRIKE_MOVEMENT_SPEED = (double) getValueOrInit("large", YmlSettings.STRIKE_MOVEMENT_SPEED.getPath());
        this.STRIKE_MOVEMENT_LAG = (int) getValueOrInit("large", YmlSettings.STRIKE_MOVEMENT_LAG.getPath());
        instance = this;
        closenessToStrikeres.get(Closeness.HIGH_CLOSE).setIsCheckStrike();
        for (UUID mob : getMobs()) {
            @Nullable Entity striker = Bukkit.getEntity(mob);
            if (striker == null) {
                MobListSql.removeMob(mob);
                continue;
            }
            Closeness closeness = determineConcern(striker);
            closenessToStrikeres.get(closeness).giveStriker(striker, 0);
        }
    }

    public static LargeOrbitalStrikeManagerTicker get() {
        return instance;
    }

    @Override
    public void eatEvent(CreatureSpawnEvent event) {
        // this is a striker
        final Entity striker = event.getEntity();
        Closeness closeness = determineConcern(striker);
        closenessToStrikeres.get(closeness).giveStriker(striker, 0);
        addMobs(striker.getUniqueId());
    }

    @Override
    public String getName() {
        return "large_orbital_strike";
    }

    @Override
    public void initializeYml() throws IOException {
        for (YmlSettings setting : YmlSettings.values()) {
            setValueIfNotExists("large",setting.getPath(), setting.value);
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


        @Nullable Player player = UpdatedPlayerList.getClosestPlayer(strikerLocation, callerUid);
        if (player == null)
            return Closeness.lowest();
        else
            return Closeness.getCloseness(strikerLocation, player.getLocation());
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
        STRIKE_COOLDOWN("cooldown", 800),
        STRIKE_DISTANCE("targetingRange", 100),
        STRIKE_TARGET_RADIUS("radius", 6d),
        STRIKE_HEIGHT("height", 20),
        STRIKE_TIME("totalTime", 300),
        STRIKE_TARGET_TIME("targetTime", 30),
        DESTRUCTION_BLAZE_INTERVAL("shootInterval", 1d),
        STRIKE_MOVEMENT_SPEED("movementSpeed", 2.2),
        STRIKE_MOVEMENT_LAG("movementTargetLag", 1);

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
