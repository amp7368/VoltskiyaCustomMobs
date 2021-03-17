package apple.voltskiya.custom_mobs.heartbeat.tick.main;

import apple.voltskiya.custom_mobs.DistanceUtils;
import apple.voltskiya.custom_mobs.heartbeat.tick.Tickable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdatedPlayerList implements Tickable {
    private static List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
    private final Map<Concern, Integer> concern = new HashMap<>();

    private static final int MIN_CONCERN = Concern.VERY_LOW_400.getTicks();
    private int tickCountdown = 0;
    private Concern currentConcern = Concern.HIGH_1;
    private final Object CONCERN_SYNC = new Object();
    private static final Object PLAYER_SYNC = new Object();

    public static List<Player> getPlayers() {
        synchronized (PLAYER_SYNC) {
            return players;
        }
    }

    @Nullable
    public static Player getCollision(BoundingBox other) {
        for (Player p : players) {
            final BoundingBox b = p.getBoundingBox();
            if (b.contains(other) ||
                    b.contains(other.getMin()) ||
                    b.contains(other.getMax())
            ) return p;
        }
        return null;
    }

    @Override
    public void tick() {
        if (tickCountdown-- == 0) {
            synchronized (CONCERN_SYNC) {
                tickCountdown = currentConcern.getTicks();
            }
            synchronized (PLAYER_SYNC) {
                players = new ArrayList<>(Bukkit.getOnlinePlayers());
            }
        }
    }

    public void raiseConcern(Concern concern) {
        synchronized (CONCERN_SYNC) {
            currentConcern = concern.getTicks() < currentConcern.getTicks() ? currentConcern : concern;
            this.concern.compute(concern, (c, v) -> v == null ? 1 : v + 1);
        }
    }

    public void lowerConcern(Concern concern) {
        synchronized (CONCERN_SYNC) {
            int newLevel = this.concern.compute(concern, (c, v) -> v == null ? 1 : Math.min(0, v - 1));
            if (currentConcern == concern && newLevel == 0) {
                // try to lower currentConcern
                while ((concern = concern.getLower()) != null) {
                    if (this.concern.getOrDefault(concern, 0) != 0) {
                        this.currentConcern = concern;
                    }
                }
            }
        }
    }

    private enum Concern {
        VERY_LOW_400(400, null),
        LOW_20(20, VERY_LOW_400),
        MEDIUM_10(10, LOW_20),
        HIGH_1(1, MEDIUM_10);

        private final int ticks;
        private final Concern lower;

        Concern(int ticks, Concern lower) {
            this.ticks = ticks;
            this.lower = lower;
        }

        public int getTicks() {
            return ticks;
        }

        public Concern getLower() {
            return lower;
        }
    }
}
