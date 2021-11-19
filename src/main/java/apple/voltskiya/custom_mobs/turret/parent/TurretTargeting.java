package apple.voltskiya.custom_mobs.turret.parent;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.utilities.util.DistanceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class TurretTargeting {
    private static final int HISTORY_SIZE = 10;
    private transient final List<Location> locationHistory = new ArrayList<>();
    public transient LivingEntity target = null;

    protected void clearLocationHistory() {
        this.locationHistory.clear();
    }

    public void tick() {
        locationHistory.add(target.getEyeLocation());
        while (locationHistory.size() > HISTORY_SIZE) {
            locationHistory.remove(0);
        }
    }

    public void clearTarget() {
        this.target = null;
        locationHistory.clear();
    }

    public LivingEntity getTarget() {
        return target;
    }

    public void setTarget(LivingEntity living) {
        if (target != living) clearLocationHistory();
        target = living;
    }

    @Nullable
    public Location getGoalLocation(Location spawnLocation, Function<Double, Double> shotSpeed) {
        if (locationHistory.isEmpty()) return null;
        Location targetEarlier = locationHistory.get(0);
        int size = locationHistory.size();
        Location targetNow = locationHistory.get(size - 1);
        Location movementPerTick = targetEarlier.clone().subtract(targetNow).multiply(1d / locationHistory.size());

        Location prediction = predict(spawnLocation, shotSpeed, targetNow, targetNow, movementPerTick);
        prediction = predict(spawnLocation, shotSpeed, targetNow, prediction, movementPerTick);
        return predict(spawnLocation, shotSpeed, targetNow, prediction, movementPerTick);
    }

    @NotNull
    private Location predict(Location spawnLocation, Function<Double, Double> shotSpeed, Location targetNow, Location expectedLocation, Location movementPerTick) {
        Location distanceToTarget = expectedLocation.clone().subtract(spawnLocation);
        double distance = DistanceUtils.magnitude(distanceToTarget);
        double time = distance / shotSpeed.apply(distance);
        return targetNow.clone().add(movementPerTick.multiply(time));
    }

    public abstract boolean shouldTargetPlayer(Player player);

    public abstract boolean isTargetHostile(LivingEntity entity);

    public abstract boolean isTargetEntity(LivingEntity entity);


}
