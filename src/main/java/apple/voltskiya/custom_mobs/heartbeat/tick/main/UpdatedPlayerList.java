package apple.voltskiya.custom_mobs.heartbeat.tick.main;

import apple.voltskiya.custom_mobs.DistanceUtils;
import apple.voltskiya.custom_mobs.heartbeat.tick.Tickable;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
            if (p.getGameMode() == GameMode.SURVIVAL) {
                BoundingBox b = p.getBoundingBox();
                Vector[] corners = getCorners(other);
                for (Vector corner : corners) if (b.contains(corner)) return p;
                if (b.contains(other)) return p;
                b = other;
                other = p.getBoundingBox();
                corners = getCorners(p.getBoundingBox());
                for (Vector corner : corners) if (b.contains(corner)) return p;
                if (other.contains(b)) return p;
            }
        }
        return null;
    }

    @Nullable
    public static Player getClosestPlayer(Location location) {
        Player closest = null;
        double distance = -1;
        for (Player p : players) {
            if (p.getGameMode() == GameMode.SURVIVAL) {
                Location pLocation = p.getLocation();
                double d = DistanceUtils.distance(location, pLocation);
                if (d < distance) {
                    distance = d;
                    closest = p;
                }
            }
        }
        return closest;
    }

    private static Vector[] getCorners(BoundingBox other) {
        Vector[] corners = new Vector[8];
        double xMin = other.getMinX();
        double yMin = other.getMinY();
        double zMin = other.getMinZ();
        double xMax = other.getMaxX();
        double yMax = other.getMaxY();
        double zMax = other.getMaxZ();

        int i = 0;
        for (double x = xMin; x <= xMax; x = xMax) {
            for (double y = yMin; y <= yMax; y = yMax) {
                for (double z = zMin; z <= zMax; z = zMax) {
                    corners[i++] = new Vector(x, y, z);
                    if (z == zMax) break;
                }
                if (y == yMax) break;
            }
            if (x == xMax) break;
        }
        return corners;
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
