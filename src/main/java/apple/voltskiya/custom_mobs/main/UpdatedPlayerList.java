package apple.voltskiya.custom_mobs.main;

import apple.voltskiya.custom_mobs.main.ticking.HighFrequencyTick;
import apple.voltskiya.custom_mobs.util.DistanceUtils;
import apple.voltskiya.custom_mobs.tick.Tickable;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdatedPlayerList implements Tickable {
    private static long callerUid;
    private static final int RECALCULATE_INTERVAL = 20 ;//* 30;
    private static int recalculateCountdown = RECALCULATE_INTERVAL;
    private static final Map<Long, Caller> callsPerInterval = new HashMap<>();

    private static Concern currentConcern = Concern.HIGH_2;
    private static int tickCountdown = 0;
    private static long tickId = 0;
    private static List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
    private static final Object CONCERN_SYNC = new Object();
    private static final Object PLAYER_SYNC = new Object();

    public UpdatedPlayerList() {
        synchronized (PLAYER_SYNC) {
            HighFrequencyTick.get().add(this::tick);
        }
    }

    @Override
    public void tick() {
        tickId++;
        if (recalculateCountdown-- == 0) {
            recalculateCountdown = RECALCULATE_INTERVAL;
            recalculateConcern();
        }
        if (tickCountdown-- == 0) {
            synchronized (CONCERN_SYNC) {
                tickCountdown = currentConcern.getTicks();
            }
            synchronized (PLAYER_SYNC) {
                players = new ArrayList<>(Bukkit.getOnlinePlayers());
            }
        }
    }

    public static synchronized long callerUid() {
        return callerUid++;
    }

    private static void call(long callerUid) {
        synchronized (CONCERN_SYNC) {
            callsPerInterval.compute(callerUid, (id, call) -> {
                if (call == null) call = new Caller(id);
                call.call();
                return call;
            });
        }
    }

    public static List<Player> getPlayers(long callerUid) {
        call(callerUid);
        synchronized (PLAYER_SYNC) {
            return players;
        }
    }

    @Nullable
    public static Player getCollision(BoundingBox other, long callerUid) {
        call(callerUid);
        synchronized (PLAYER_SYNC) {
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
    }

    @Nullable
    public static Player getClosestPlayer(Location location, long callerUid) {
        call(callerUid);
        synchronized (PLAYER_SYNC) {
            Player closest = null;
            double distance = Integer.MAX_VALUE;
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

    private void recalculateConcern() {
        synchronized (CONCERN_SYNC) {
            int maxCalls = 0;
            for (Caller caller : callsPerInterval.values()) {
                maxCalls = Math.max(caller.getCallsRecently(), maxCalls);
            }
            double callsPerTick = ((double) maxCalls) / RECALCULATE_INTERVAL;
            currentConcern = Concern.getConcern(callsPerTick);
        }
    }

    private enum Concern {
        HIGH_2(2, null),
        MEDIUM_10(10, HIGH_2),
        LOW_20(20, MEDIUM_10),
        VERY_LOW_400(200, LOW_20);

        private final int ticks;
        private final Concern upper;

        Concern(int ticks, Concern upper) {
            this.ticks = ticks;
            this.upper = upper;
        }

        private boolean shouldNext(double callsPerTick) {
            return 1d / ticks < callsPerTick/2; // just some bias /2
        }

        public int getTicks() {
            return ticks;
        }

        @NotNull
        public static Concern getConcern(double callsPerTick) {
            Concern proper = VERY_LOW_400;
            while (proper.upper != null && proper.shouldNext(callsPerTick)) {
                proper = proper.upper;
            }
            return proper;
        }

    }

    private static class Caller {
        private final long id;
        private long lastCall = -1;
        private int callsRecently = 0;

        public Caller(long id) {
            this.id = id;
        }

        private void call() {
            if (tickId != lastCall) {
                lastCall = tickId;
                callsRecently++;
            }
        }

        private int getCallsRecently() {
            int c = callsRecently;
            callsRecently = 0;
            return c;
        }
    }
}
