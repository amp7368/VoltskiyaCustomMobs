package apple.voltskiya.custom_mobs.util;

import apple.mc.utilities.world.vector.VectorUtils;
import apple.utilities.util.ObjectUtilsFormatting;
import apple.voltskiya.custom_mobs.abilities.tick.Tickable;
import apple.voltskiya.custom_mobs.util.ticking.HighFrequencyTick;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UpdatedPlayerList implements Tickable {

    private static List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

    public UpdatedPlayerList() {
        HighFrequencyTick.get().add(this::tick);
    }

    @NotNull
    public static List<Player> getNearbyPlayers(Location location, double distance) {
        List<Player> nearby = new ArrayList<>();
        for (Player player : players) {
            if (VectorUtils.distance(location, player.getLocation()) <= distance) {
                nearby.add(player);
            }
        }
        return nearby;
    }

    public static List<Player> getPlayers() {
        return List.copyOf(players);
    }

    @Nullable
    public static Player getCollision(BoundingBox other) {
        for (Player p : players) {
            if (p.getGameMode() == GameMode.SURVIVAL && other.overlaps(p.getBoundingBox()))
                return p;
        }
        return null;
    }

    @Nullable
    public static Player getClosestPlayerPlayer(Location location) {
        @Nullable PlayerClose player = getClosestPlayer(location);
        return ObjectUtilsFormatting.defaultIfNull(null, player, PlayerClose::player);
    }

    @NotNull
    public static PlayerClose getClosestPlayer(Location location) {
        Player closest = null;
        double distance = Double.MAX_VALUE;
        for (Player p : players) {
            Location pLocation = p.getLocation();
            double d = VectorUtils.distance(location, pLocation);
            if (d < distance) {
                distance = d;
                closest = p;
            }
        }
        return new PlayerClose(closest, distance);
    }

    @Nullable
    public static PlayerClose getClosestPlayerInGamemode(Location location, GameMode... gameModes) {
        Player closest = null;
        double distance = Double.MAX_VALUE;
        for (Player p : players) {
            GameMode playerGamemode = p.getGameMode();
            boolean isNotInGamemode = true;
            for (GameMode gamemodeAllowed : gameModes) {
                if (playerGamemode == gamemodeAllowed) {
                    isNotInGamemode = false;
                    break;
                }
            }
            if (isNotInGamemode)
                continue;
            Location pLocation = p.getLocation();
            double d = VectorUtils.distance(location, pLocation);
            if (d < distance) {
                distance = d;
                closest = p;
            }
        }
        return closest == null ? null : new PlayerClose(closest, distance);
    }

    @Override
    public void tick() {
        players = ImmutableList.copyOf(Bukkit.getOnlinePlayers());
    }

}
