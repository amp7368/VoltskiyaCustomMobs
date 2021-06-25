package apple.voltskiya.custom_mobs.util;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.Tickable;
import apple.voltskiya.custom_mobs.ticking.HighFrequencyTick;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class UpdatedPlayerList implements Tickable {
    private static List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
    private static final Object PLAYER_SYNC = new Object();

    public UpdatedPlayerList() {
        synchronized (PLAYER_SYNC) {
            HighFrequencyTick.get().add(this::tick);
        }
    }

    @NotNull
    public static List<Player> getNearbyPlayers(Location location, double distance) {
        List<Player> nearby = new ArrayList<>();
        for (Player player : players) {
            if (DistanceUtils.distance(location, player.getLocation()) <= distance) {
                nearby.add(player);
            }
        }
        return nearby;
    }

    @Override
    public void tick() {
        synchronized (PLAYER_SYNC) {
            players = new ArrayList<>(Bukkit.getOnlinePlayers());
        }
    }

    public static List<Player> getPlayers() {
        synchronized (PLAYER_SYNC) {
            return players;
        }
    }

    @Nullable
    public static Player getCollision(BoundingBox other) {
        synchronized (PLAYER_SYNC) {
            for (Player p : players) {
                if (p.getGameMode() == GameMode.SURVIVAL && other.overlaps(p.getBoundingBox())) return p;
            }
            return null;
        }
    }

    @Nullable
    public static Player getClosestPlayer(Location location) {
        synchronized (PLAYER_SYNC) {
            Player closest = null;
            double distance = Double.MAX_VALUE;
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
}
