package apple.voltskiya.custom_mobs.heartbeat.tick.lost_soul;

import apple.voltskiya.custom_mobs.DistanceUtils;
import apple.voltskiya.custom_mobs.heartbeat.tick.SpawnEater;
import apple.voltskiya.custom_mobs.heartbeat.tick.main.*;
import apple.voltskiya.custom_mobs.heartbeat.tick.orbital_strike.OrbitalStrikeManagerTicker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vex;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.List;

public class LostSoulManagerTicker extends SpawnEater {
    protected final double DAMAGE_AMOUNT;
    private static LostSoulManagerTicker instance;
    private final Map<Closeness, LostSoulIndividualTicker> closenessToVexes = new HashMap<>() {{
        for (Closeness closeness : Closeness.values())
            put(closeness, new LostSoulIndividualTicker(closeness.getGiver(), closeness));
    }};

    public LostSoulManagerTicker() throws IOException {
        instance = this;
        closenessToVexes.get(Closeness.HIGH_CLOSE).setIsCheckCollision();
        DAMAGE_AMOUNT = (double) getValueOrInit(YmlSettings.DAMAGE_AMOUNT.getPath());
        for (UUID mob : getMobs()) {
            @Nullable Entity striker = Bukkit.getEntity(mob);
            if (!(striker instanceof Vex)) continue;
            Closeness closeness = determineConcern((Vex) striker);
            closenessToVexes.get(closeness).giveVex((Vex) striker);
        }
    }

    public static LostSoulManagerTicker get() {
        return instance;
    }

    @Override
    public void eatEvent(CreatureSpawnEvent event) {
        if (event.getEntity().getType() == EntityType.VEX) {
            // this is a vex
            final Vex vex = (Vex) event.getEntity();
            Closeness closeness = determineConcern(vex);
            closenessToVexes.get(closeness).giveVex(vex);
            addMobs(vex.getUniqueId());
        }
    }

    @Override
    public String getName() {
        return "lost_soul";
    }

    @Override
    public void initializeYml() throws IOException {
        setValueIfNotExists("damageAmount", 2d);
    }


    public boolean amIGivingVex(Vex vex, Closeness currentCloseness) {
        Closeness actualCloseness = determineConcern(vex);
        if (actualCloseness != currentCloseness) {
            closenessToVexes.get(actualCloseness).giveVex(vex);
            return true;
        }
        return false;
    }

    private Closeness determineConcern(Vex vex) {
        Location vexLocation = vex.getLocation();

        List<Player> players = UpdatedPlayerList.getPlayers();
        for (Player player : players) {
            Location playerLocation = player.getLocation();
            return Closeness.getCloseness(vexLocation, playerLocation);
        }
        return Closeness.lowest();
    }

    enum Closeness {
        HIGH_CLOSE(30, HighFrequencyTick.get()),
        NORMAL_CLOSE(60, NormalFrequencyTick.get()),
        LOW_CLOSE(100, LowFrequencyTick.get());

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
        DAMAGE_AMOUNT("damageAmount", 2d);

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
