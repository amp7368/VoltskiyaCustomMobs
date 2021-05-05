package apple.voltskiya.custom_mobs.abilities.tick.hell_blazer;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.abilities.MobTickPlugin;
import apple.voltskiya.custom_mobs.abilities.tick.SpawnEater;
import apple.voltskiya.custom_mobs.ticking.HighFrequencyTick;
import apple.voltskiya.custom_mobs.ticking.LowFrequencyTick;
import apple.voltskiya.custom_mobs.ticking.NormalFrequencyTick;
import apple.voltskiya.custom_mobs.ticking.TickGiverable;
import apple.voltskiya.custom_mobs.util.DistanceUtils;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HellGuardManagerTicker extends SpawnEater {
    private static HellGuardManagerTicker instance;
    private final Map<Closeness, HellGuardIndividualTicker> closenessToHellBlazeres = new HashMap<>() {{
        for (Closeness closeness : Closeness.values())
            put(closeness, new HellGuardIndividualTicker(closeness.getGiver(), closeness));
    }};

    public HellGuardManagerTicker() throws IOException {
        instance = this;
        for (UUID mob : getMobs()) {
            @Nullable Entity hellBlazer = Bukkit.getEntity(mob);
            if (!(hellBlazer instanceof LivingEntity)) continue;
            Closeness closeness = determineConcern((LivingEntity) hellBlazer);
            closenessToHellBlazeres.get(closeness).giveHellBlazer((LivingEntity) hellBlazer);
        }
    }

    public static HellGuardManagerTicker get() {
        return instance;
    }

    @Override
    public void eatEvent(CreatureSpawnEvent event) {
        // this is a hellBlazer
        final LivingEntity hellBlazer = event.getEntity();
        Closeness closeness = determineConcern(hellBlazer);
        closenessToHellBlazeres.get(closeness).giveHellBlazer(hellBlazer);
        addMobs(hellBlazer.getUniqueId());
    }

    @Override
    public String getName() {
        return "breakable";
    }

    @Override
    public apple.voltskiya.custom_mobs.YmlSettings[] getSettings() {
        return YmlSettings.values();
    }

    @Override
    public void initializeYml() throws IOException {
        setValueIfNotExists("damageAmount", 2d);
    }

    @Override
    protected VoltskiyaModule getPlugin() {
        return MobTickPlugin.get();
    }


    public boolean amIGivingHellBlazer(LivingEntity hellBlazer, Closeness currentCloseness) {
        Closeness actualCloseness = determineConcern(hellBlazer);
        if (actualCloseness != currentCloseness) {
            closenessToHellBlazeres.get(actualCloseness).giveHellBlazer(hellBlazer);
            return true;
        }
        return false;
    }

    private Closeness determineConcern(LivingEntity hellBlazer) {
        Location hellBlazerLocation = hellBlazer.getLocation();

        List<Player> players = UpdatedPlayerList.getPlayers();
        for (Player player : players) {
            Location playerLocation = player.getLocation();
            return Closeness.getCloseness(hellBlazerLocation, playerLocation);
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


    private enum YmlSettings implements apple.voltskiya.custom_mobs.YmlSettings {
        ;

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
