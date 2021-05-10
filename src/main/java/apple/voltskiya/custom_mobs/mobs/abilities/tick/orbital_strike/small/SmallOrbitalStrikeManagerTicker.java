package apple.voltskiya.custom_mobs.mobs.abilities.tick.orbital_strike.small;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.mobs.ConfigManager;
import apple.voltskiya.custom_mobs.mobs.RegisteredEntityEater;
import apple.voltskiya.custom_mobs.mobs.abilities.MobTickPlugin;
import apple.voltskiya.custom_mobs.ticking.LowFrequencyTick;
import apple.voltskiya.custom_mobs.ticking.NormalFrequencyTick;
import apple.voltskiya.custom_mobs.ticking.TickGiverable;
import apple.voltskiya.custom_mobs.ticking.VeryLowFrequencyTick;
import apple.voltskiya.custom_mobs.util.DistanceUtils;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SmallOrbitalStrikeManagerTicker extends ConfigManager implements RegisteredEntityEater {
    public final double STRIKE_MIN_HEIGHT;
    public final double STRIKE_MOVEMENT_SPEED;
    public final double STRIKE_MOVEMENT_LAG;
    public final double STRIKE_CHANCE;
    public final long STRIKE_COOLDOWN;
    public final double STRIKE_DISTANCE;

    public final double STRIKE_TARGET_LARGER_RADIUS;
    public final double STRIKE_TARGET_RADIUS;
    public final double STRIKE_HEIGHT;
    public final int STRIKE_TIME;
    public final int STRIKE_TARGET_TIME;
    public final double DESTRUCTION_BLAZE_INTERVAL;

    private static SmallOrbitalStrikeManagerTicker instance;
    private final Map<Closeness, SmallOrbitalStrikeIndividualTicker> closenessToStrikeres = new HashMap<>() {{
        for (Closeness closeness : Closeness.values())
            put(closeness, new SmallOrbitalStrikeIndividualTicker(closeness));
    }};

    public SmallOrbitalStrikeManagerTicker() throws IOException {
        this.STRIKE_CHANCE = (double) getValueOrInit("small", YmlSettings.STRIKE_CHANCE.getPath());
        this.STRIKE_COOLDOWN = (1000L / 20 * (int) getValueOrInit("small", YmlSettings.STRIKE_COOLDOWN.getPath()));
        this.STRIKE_DISTANCE = (int) getValueOrInit("small", YmlSettings.STRIKE_DISTANCE.getPath());
        this.STRIKE_TARGET_RADIUS = (double) getValueOrInit("small", YmlSettings.STRIKE_TARGET_RADIUS.getPath());
        this.STRIKE_TARGET_LARGER_RADIUS = (double) getValueOrInit("small", YmlSettings.STRIKE_TARGET_LARGER_RADIUS.getPath());
        this.STRIKE_HEIGHT = (int) getValueOrInit("small", YmlSettings.STRIKE_HEIGHT.getPath());
        this.STRIKE_MIN_HEIGHT = (int) getValueOrInit("small", YmlSettings.STRIKE_MIN_HEIGHT.getPath());
        this.STRIKE_TIME = (int) getValueOrInit("small", YmlSettings.STRIKE_TIME.getPath());
        this.STRIKE_TARGET_TIME = (int) getValueOrInit("small", YmlSettings.STRIKE_TARGET_TIME.getPath());
        this.DESTRUCTION_BLAZE_INTERVAL = (double) getValueOrInit("small", YmlSettings.DESTRUCTION_BLAZE_INTERVAL.getPath());
        this.STRIKE_MOVEMENT_SPEED = (double) getValueOrInit("small", YmlSettings.STRIKE_MOVEMENT_SPEED.getPath());
        this.STRIKE_MOVEMENT_LAG = (int) getValueOrInit("small", YmlSettings.STRIKE_MOVEMENT_LAG.getPath());
        instance = this;
        closenessToStrikeres.get(Closeness.HIGH_CLOSE).setIsCheckStrike();
    }

    public static SmallOrbitalStrikeManagerTicker get() {
        return instance;
    }

    @Override
    public void eatEntity(Entity striker) {
        // this is a striker
        Closeness closeness = determineConcern(striker);
        closenessToStrikeres.get(closeness).giveStriker(striker, 0);
        addMob(striker.getUniqueId());
    }

    @Override
    public String getName() {
        return "small_orbital_strike";
    }

    @Override
    public apple.voltskiya.custom_mobs.mobs.YmlSettings[] getSettings() {
        return YmlSettings.values();
    }

    @Override
    public void initializeYml() throws IOException {
        for (YmlSettings setting : YmlSettings.values()) {
            setValueIfNotExists("small", setting.getPath(), setting.value);
        }
    }

    @Override
    protected VoltskiyaModule getPlugin() {
        return MobTickPlugin.get();
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


        @Nullable Player player = UpdatedPlayerList.getClosestPlayer(strikerLocation);
        if (player == null)
            return Closeness.lowest();
        else
            return Closeness.getCloseness(strikerLocation, player.getLocation());
    }

    enum Closeness {
        HIGH_CLOSE(50, NormalFrequencyTick.get()),
        NORMAL_CLOSE(100, LowFrequencyTick.get()),
        LOW_CLOSE(200, VeryLowFrequencyTick.get());

        private final double distance;
        private static final Closeness[] order = new Closeness[]{HIGH_CLOSE, NORMAL_CLOSE, LOW_CLOSE};
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

    private enum YmlSettings implements apple.voltskiya.custom_mobs.mobs.YmlSettings {
        STRIKE_CHANCE("chancePerTick", 0.003d),
        STRIKE_COOLDOWN("cooldown", 800),
        STRIKE_DISTANCE("targetingRange", 30),
        STRIKE_TARGET_RADIUS("radius", 1d),
        STRIKE_HEIGHT("height", 15),
        STRIKE_MIN_HEIGHT("min_height", 5),
        STRIKE_TIME("totalTime", 200),
        STRIKE_TARGET_TIME("targetTime", 40),
        DESTRUCTION_BLAZE_INTERVAL("shootInterval", 3d),
        STRIKE_MOVEMENT_SPEED("movementSpeed", 0d),
        STRIKE_MOVEMENT_LAG("movementTargetLag", 0),
        STRIKE_TARGET_LARGER_RADIUS("targetVariationRadius", 8.0);

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
