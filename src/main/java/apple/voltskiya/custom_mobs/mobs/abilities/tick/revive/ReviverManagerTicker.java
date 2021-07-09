package apple.voltskiya.custom_mobs.mobs.abilities.tick.revive;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.mobs.ConfigManager;
import apple.voltskiya.custom_mobs.mobs.RegisteredEntityEater;
import apple.voltskiya.custom_mobs.mobs.abilities.MobTickPlugin;
import apple.voltskiya.custom_mobs.ticking.NormalFrequencyTick;
import apple.voltskiya.custom_mobs.ticking.TickGiverable;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.utilities.util.DistanceUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReviverManagerTicker extends ConfigManager implements RegisteredEntityEater {
    public int REVIVE_RITUAL_TIME;
    public double REVIVE_DISTANCE;
    public double REVIVE_CHANCE;
    private final Map<Closeness, ReviverIndividualTicker> closenessToReviveres = new HashMap<>() {{
        for (Closeness closeness : Closeness.values())
            put(closeness, new ReviverIndividualTicker(closeness));
        get(Closeness.HIGH_CLOSE).setIsReviving();
    }};
    private static ReviverManagerTicker instance;


    public ReviverManagerTicker() throws IOException {
        instance = this;
        REVIVE_CHANCE = (double) getValueOrInit(getName(), YmlSettings.REVIVE_CHANCE.getPath(), "reviver");
        REVIVE_DISTANCE = (int) getValueOrInit(getName(), YmlSettings.REVIVE_DISTANCE.getPath(), "reviver");
        REVIVE_RITUAL_TIME = (int) getValueOrInit(getName(), YmlSettings.REVIVE_RITUAL_TIME.getPath(), "reviver");
    }

    public static ReviverManagerTicker get() {
        return instance;
    }

    /**
     * eat an entity
     * please override one of the eatEntity events
     * (there's 3 cause it makes things easier and lets the programmer choose what they need)
     *
     * @param reviver the entity to eat
     */
    @Override
    public void eatEntity(Entity reviver) {
        // this is a reviver
        Closeness closeness = determineConcern(reviver);
        closenessToReviveres.get(closeness).giveReviver(new Reviver(reviver, REVIVE_DISTANCE));
    }

    @Override
    public String getName() {
        return "revive";
    }

    @Override
    public apple.voltskiya.custom_mobs.mobs.YmlSettings[] getSettings() {
        return YmlSettings.values();
    }

    @Override
    public void initializeYml() throws IOException {
        for (YmlSettings setting : YmlSettings.values()) {
            setValueIfNotExists(getName(), setting.getPath(), setting.value, "reviver");
        }
    }

    @Override
    protected VoltskiyaModule getPlugin() {
        return MobTickPlugin.get();
    }


    public boolean amIGivingReviver(Reviver reviver, Closeness currentCloseness) {
        Closeness actualCloseness = determineConcern(reviver.getEntity());
        if (actualCloseness != currentCloseness) {
            closenessToReviveres.get(actualCloseness).giveReviver(reviver);
            return true;
        }
        return false;
    }

    @NotNull
    private Closeness determineConcern(@Nullable Entity reviver) {
        if (reviver == null) return Closeness.lowest();
        Location reviverLocation = reviver.getLocation();

        @Nullable Player player = UpdatedPlayerList.getClosestPlayer(reviverLocation);
        return player == null ? Closeness.lowest() : Closeness.getCloseness(reviverLocation, player.getLocation());
    }

    enum Closeness {
        HIGH_CLOSE(15, NormalFrequencyTick.get());

        private final double distance;
        private static final Closeness[] order = new Closeness[]{HIGH_CLOSE};
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
        REVIVE_CHANCE("reviveChance", 0.125),
        REVIVE_DISTANCE("reviveDistance", 50),
        REVIVE_RITUAL_TIME("reviveRitualTime", 20);

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
