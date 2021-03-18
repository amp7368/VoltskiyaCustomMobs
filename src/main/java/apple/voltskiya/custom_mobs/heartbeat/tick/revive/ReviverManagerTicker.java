package apple.voltskiya.custom_mobs.heartbeat.tick.revive;

import apple.voltskiya.custom_mobs.DistanceUtils;
import apple.voltskiya.custom_mobs.heartbeat.tick.MobListSql;
import apple.voltskiya.custom_mobs.heartbeat.tick.SpawnEater;
import apple.voltskiya.custom_mobs.heartbeat.tick.main.*;
import apple.voltskiya.custom_mobs.heartbeat.tick.orbital_strike.OrbitalStrikeManagerTicker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ReviverManagerTicker extends SpawnEater {

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
        for (UUID mob : getMobs()) {
            @Nullable Entity striker = Bukkit.getEntity(mob);
            if (striker == null) {
                MobListSql.removeMob(mob);
                continue;
            }
            Closeness c = determineConcern(striker);
            closenessToReviveres.get(c).giveReviver(striker);
        }
    }

    public static ReviverManagerTicker get() {
        return instance;
    }

    @Override
    public void eatEvent(CreatureSpawnEvent event) {
        // this is a reviver
        final Entity reviver = event.getEntity();
        Closeness closeness = determineConcern(reviver);
        closenessToReviveres.get(closeness).giveReviver(reviver);
        addMobs(reviver.getUniqueId());
    }

    @Override
    public String getName() {
        return "revive";
    }

    @Override
    public void initializeYml() throws IOException {
        for (YmlSettings setting : YmlSettings.values()) {
            setValueIfNotExists(getName(), setting.getPath(), setting.value, "reviver");
        }
    }


    public boolean amIGivingReviver(Entity entity, Closeness currentCloseness) {
        Closeness actualCloseness = determineConcern(entity);
        if (actualCloseness != currentCloseness) {
            closenessToReviveres.get(actualCloseness).giveReviver(entity);
            return true;
        }
        return false;
    }

    private Closeness determineConcern(Entity reviver) {
        Location reviverLocation = reviver.getLocation();

        List<Player> players = UpdatedPlayerList.getPlayers();
        for (Player player : players) {
            Location playerLocation = player.getLocation();
            return Closeness.getCloseness(reviverLocation, playerLocation);
        }
        return Closeness.lowest();
    }

    enum Closeness {
        HIGH_CLOSE(20, NormalFrequencyTick.get()),
        NORMAL_CLOSE(50, LowFrequencyTick.get()),
        LOW_CLOSE(70, VeryLowFrequencyTick.get());

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

    private enum YmlSettings {
        REVIVE_CHANCE("reviveChance", .01d),
        REVIVE_DISTANCE("reviveDistance", 10);

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
