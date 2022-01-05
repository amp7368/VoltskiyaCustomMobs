package apple.voltskiya.custom_mobs.mobs.abilities.tick.orbital_strike.large;

import apple.voltskiya.custom_mobs.mobs.abilities.MobTickPlugin;
import apple.voltskiya.custom_mobs.mobs.nms.parent.config.ConfigManager;
import apple.voltskiya.custom_mobs.mobs.nms.parent.register.RegisteredEntityEater;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;
import apple.voltskiya.custom_mobs.util.ticking.LowFrequencyTick;
import apple.voltskiya.custom_mobs.util.ticking.TickGiverable;
import apple.voltskiya.custom_mobs.util.ticking.VeryLowFrequencyTick;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;
import voltskiya.apple.utilities.util.DistanceUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LargeOrbitalStrikeManagerTicker extends ConfigManager implements RegisteredEntityEater {
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
    }

    public static LargeOrbitalStrikeManagerTicker get() {
        return instance;
    }


    /**
     * eat an entity
     * please override one of the eatEntity events
     * (there's 3 cause it makes things easier and lets the programmer choose what they need)
     *
     * @param striker the entity to eat
     */
    @Override
    public void eatEntity(Entity striker) {
        // this is a striker
        Closeness closeness = determineConcern(striker);
        closenessToStrikeres.get(closeness).giveStriker(striker, 0);
    }


    @Override
    public String getName() {
        return "large_orbital_strike";
    }

    @Override
    public apple.voltskiya.custom_mobs.mobs.nms.parent.config.YmlSettings[] getSettings() {
        return YmlSettings.values();
    }

    @Override
    public void initializeYml() throws IOException {
        for (YmlSettings setting : YmlSettings.values()) {
            setValueIfNotExists("large", setting.getPath(), setting.value);
        }
    }

    @Override
    protected PluginManagedModule getPlugin() {
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


        @Nullable Player player = UpdatedPlayerList.getClosestPlayerPlayer(strikerLocation);
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

    private enum YmlSettings implements apple.voltskiya.custom_mobs.mobs.nms.parent.config.YmlSettings {
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
