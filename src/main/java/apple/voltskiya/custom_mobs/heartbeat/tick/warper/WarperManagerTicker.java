package apple.voltskiya.custom_mobs.heartbeat.tick.warper;

import apple.voltskiya.custom_mobs.DistanceUtils;
import apple.voltskiya.custom_mobs.heartbeat.tick.SpawnEater;
import apple.voltskiya.custom_mobs.heartbeat.tick.main.*;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarperManagerTicker extends SpawnEater {
    public int PARTICLES;
    public int WARP_RADIUS;
    public double WARP_CHANCE;
    private final Map<WarperManagerTicker.Closeness, WarperIndividualTicker> closenessToWarperes = new HashMap<>() {{
        for (WarperManagerTicker.Closeness closeness : WarperManagerTicker.Closeness.values())
            put(closeness, new WarperIndividualTicker(closeness));
        get(Closeness.HIGH_CLOSE).setIsWarping();
    }};
    private static WarperManagerTicker instance;
    private final long callerUid = UpdatedPlayerList.callerUid();

    public WarperManagerTicker() throws IOException {
        instance = this;
        WARP_CHANCE = (double) getValueOrInit(YmlSettings.WARP_CHANCE.getPath());
        WARP_RADIUS = (int) getValueOrInit(YmlSettings.WARP_RADIUS.getPath());
        PARTICLES = (int) getValueOrInit(YmlSettings.PARTICLES.getPath());
    }

    public static WarperManagerTicker get() {
        return instance;
    }

    @Override
    public void eatEvent(CreatureSpawnEvent event) {
        // this is a warper
        final Entity warper = event.getEntity();
        WarperManagerTicker.Closeness closeness = determineConcern(warper);
        closenessToWarperes.get(closeness).giveWarper(warper);
        addMobs(warper.getUniqueId());
    }

    @Override
    public String getName() {
        return "warper";
    }

    @Override
    public void initializeYml() throws IOException {
        for (WarperManagerTicker.YmlSettings setting : WarperManagerTicker.YmlSettings.values()) {
            setValueIfNotExists(setting.getPath(), setting.value);
        }
    }

    public boolean amIGivingWarper(Entity entity, WarperManagerTicker.Closeness currentCloseness) {
        WarperManagerTicker.Closeness actualCloseness = determineConcern(entity);
        if (actualCloseness != currentCloseness) {
            closenessToWarperes.get(actualCloseness).giveWarper(entity);
            return true;
        }
        return false;
    }

    private WarperManagerTicker.Closeness determineConcern(Entity warper) {
        Location warperLocation = warper.getLocation();

        @Nullable Player player = UpdatedPlayerList.getClosestPlayer(warperLocation, callerUid);
        if (player == null)
            return WarperManagerTicker.Closeness.lowest();
        else
            return WarperManagerTicker.Closeness.getCloseness(warperLocation, player.getLocation());
    }

    enum Closeness {
        HIGH_CLOSE(30, HighFrequencyTick.get()),
        NORMAL_CLOSE(40, NormalFrequencyTick.get()),
        LOW_CLOSE(70, LowFrequencyTick.get());

        private final double distance;
        private static final WarperManagerTicker.Closeness[] order = new WarperManagerTicker.Closeness[]{HIGH_CLOSE, NORMAL_CLOSE, LOW_CLOSE};
        private final TickGiverable giver;

        Closeness(double distance, TickGiverable giver) {
            this.distance = distance;
            this.giver = giver;
        }

        private static WarperManagerTicker.Closeness getCloseness(Location aLocation, Location bLocation) {
            double d = DistanceUtils.distance(aLocation, bLocation);
            for (WarperManagerTicker.Closeness closeness : order) {
                if (closeness.distance >= d) {
                    return closeness;
                }
            }
            return lowest();
        }

        public static WarperManagerTicker.Closeness lowest() {
            return order[order.length - 1];
        }

        public TickGiverable getGiver() {
            return giver;
        }
    }

    private enum YmlSettings {
        WARP_RADIUS("warpRadius", 10),
        WARP_CHANCE("warpChance", .04d),
        PARTICLES("particles", 40);

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

