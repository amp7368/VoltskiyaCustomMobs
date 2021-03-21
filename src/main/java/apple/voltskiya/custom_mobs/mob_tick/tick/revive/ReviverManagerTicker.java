package apple.voltskiya.custom_mobs.mob_tick.tick.revive;

import apple.voltskiya.custom_mobs.ticking.NormalFrequencyTick;
import apple.voltskiya.custom_mobs.ticking.TickGiverable;
import apple.voltskiya.custom_mobs.util.DistanceUtils;
import apple.voltskiya.custom_mobs.mob_tick.tick.MobListSql;
import apple.voltskiya.custom_mobs.mob_tick.tick.SpawnEater;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReviverManagerTicker extends SpawnEater {

    public int REVIVE_RITUAL_TIME;
    public double REVIVE_DISTANCE;
    public double REVIVE_CHANCE;
    private final Map<Closeness, ReviverIndividualTicker> closenessToReviveres = new HashMap<>() {{
        for (Closeness closeness : Closeness.values())
            put(closeness, new ReviverIndividualTicker(closeness));
        get(Closeness.HIGH_CLOSE).setIsReviving();
    }};
    private static ReviverManagerTicker instance;
    private final long callerUid = UpdatedPlayerList.callerUid();


    public ReviverManagerTicker() throws IOException {
        instance = this;
        REVIVE_CHANCE = (double) getValueOrInit(getName(), YmlSettings.REVIVE_CHANCE.getPath(), "reviver");
        REVIVE_DISTANCE = (int) getValueOrInit(getName(), YmlSettings.REVIVE_DISTANCE.getPath(), "reviver");
        REVIVE_RITUAL_TIME = (int) getValueOrInit(getName(), YmlSettings.REVIVE_RITUAL_TIME.getPath(), "reviver");
        for (UUID mob : getMobs()) {
            @Nullable Entity reviver = Bukkit.getEntity(mob);
            if (reviver == null) {
                MobListSql.removeMob(mob);
                continue;
            }
            Closeness c = determineConcern(reviver);
            closenessToReviveres.get(c).giveReviver(new Reviver(reviver));
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
        closenessToReviveres.get(closeness).giveReviver(new Reviver(reviver));
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


    public boolean amIGivingReviver(Reviver reviver, Closeness currentCloseness) {
        Closeness actualCloseness = determineConcern(reviver.getEntity());
        if (actualCloseness != currentCloseness) {
            closenessToReviveres.get(actualCloseness).giveReviver(reviver);
            return true;
        }
        return false;
    }

    @NotNull
    private Closeness determineConcern(@Nullable Entity reviver) {
        if (reviver == null) return Closeness.lowest();
        Location reviverLocation = reviver.getLocation();

        @Nullable Player player = UpdatedPlayerList.getClosestPlayer(reviverLocation, callerUid);
        return player == null ? Closeness.lowest() : Closeness.getCloseness(reviverLocation, player.getLocation());
    }

    enum Closeness {
        HIGH_CLOSE(15, NormalFrequencyTick.get()),
        NORMAL_CLOSE(50, NormalFrequencyTick.get()),
        LOW_CLOSE(70, NormalFrequencyTick.get());

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
        REVIVE_CHANCE("reviveChance", 0.03),
        REVIVE_DISTANCE("reviveDistance", 15),
        REVIVE_RITUAL_TIME("reviveRitualTime", 13);

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
