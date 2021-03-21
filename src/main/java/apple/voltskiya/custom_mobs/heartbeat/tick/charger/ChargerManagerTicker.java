package apple.voltskiya.custom_mobs.heartbeat.tick.charger;

import apple.voltskiya.custom_mobs.DistanceUtils;
import apple.voltskiya.custom_mobs.heartbeat.tick.SpawnEater;
import apple.voltskiya.custom_mobs.heartbeat.tick.main.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChargerManagerTicker extends SpawnEater {
    public static long CHARGE_STUN_TIME;
    public static int CHARGE_UP_TIME;
    public static long CHARGE_COOLDOWN;
    public static double CHARGE_CHANCE;
    public static int MAX_CHARGE_TIME;
    public static long CHARGE_TIRED_TIME;
    protected static double TOO_CLOSE_TO_CHARGE;
    protected static double MARGIN_OF_ERROR;
    protected static double OVERSHOOT_SPEED;
    protected static int OVERSHOOT_DISTANCE;
    private static ChargerManagerTicker instance;
    private final Map<Closeness, ChargerIndividualTicker> closenessToChargeres = new HashMap<>() {{
        for (Closeness closeness : Closeness.values())
            put(closeness, new ChargerIndividualTicker(closeness.getGiver(), closeness));
        get(Closeness.HIGH_CHARGE_CLOSE).setChargeTick();
    }};
    private final long callerUid = UpdatedPlayerList.callerUid();

    public ChargerManagerTicker() throws IOException {
        instance = this;
        OVERSHOOT_DISTANCE = (int) getValueOrInit(YmlSettings.OVERSHOOT_DISTANCE.getPath());
        OVERSHOOT_SPEED = (double) getValueOrInit(YmlSettings.OVERSHOOT_SPEED.getPath());
        TOO_CLOSE_TO_CHARGE = (double) getValueOrInit(YmlSettings.TOO_CLOSE_TO_CHARGE.getPath());
        MARGIN_OF_ERROR = (double) getValueOrInit(YmlSettings.MARGIN_OF_ERROR.getPath());
        MAX_CHARGE_TIME = (int) getValueOrInit(YmlSettings.MAX_CHARGE_TIME.getPath());
        CHARGE_CHANCE = (double) getValueOrInit(YmlSettings.CHARGE_CHANCE.getPath());
        CHARGE_COOLDOWN = ((int) getValueOrInit(YmlSettings.CHARGE_COOLDOWN.getPath())) * 1000 / 20;
        CHARGE_UP_TIME = ((int) getValueOrInit(YmlSettings.CHARGE_UP_TIME.getPath()));
        CHARGE_STUN_TIME = ((int) getValueOrInit(YmlSettings.CHARGE_STUN_TIME.getPath()));
        CHARGE_TIRED_TIME = ((int) getValueOrInit(YmlSettings.CHARGE_TIRED_TIME.getPath()));
        for (UUID mob : getMobs()) {
            @Nullable Entity striker = Bukkit.getEntity(mob);
            if (!(striker instanceof Mob)) continue;
            Closeness closeness = determineConcern((Mob) striker);
            closenessToChargeres.get(closeness).giveCharger((Mob) striker);
        }
    }

    public static ChargerManagerTicker get() {
        return instance;
    }

    @Override
    public void eatEvent(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Mob) {
            // this is a charger
            final Mob charger = (Mob) event.getEntity();
            Closeness closeness = determineConcern(charger);
            closenessToChargeres.get(closeness).giveCharger(charger);
            addMobs(charger.getUniqueId());
        }
    }

    public void eatMob(Mob charger) {
        // this is a charger
        Closeness closeness = determineConcern(charger);
        closenessToChargeres.get(closeness).giveCharger(charger);
        addMobs(charger.getUniqueId());
    }

    @Override
    public String getName() {
        return "charger";
    }

    @Override
    public void initializeYml() throws IOException {
        for (YmlSettings setting : YmlSettings.values())
            setValueIfNotExists(setting.getPath(), setting.getValue());
    }


    public boolean amIGivingCharger(Mob charger, Closeness currentCloseness) {
        Closeness actualCloseness = determineConcern(charger);
        if (actualCloseness != currentCloseness) {
            closenessToChargeres.get(actualCloseness).giveCharger(charger);
            return true;
        }
        return false;
    }

    private Closeness determineConcern(Mob charger) {
        Location chargerLocation = charger.getLocation();

        @Nullable Player player = UpdatedPlayerList.getClosestPlayer(chargerLocation, callerUid);
        if (player == null)
            return Closeness.lowest();
        else
            return Closeness.getCloseness(chargerLocation, player.getLocation());
    }

    enum Closeness {
        HIGH_CHARGE_CLOSE(15, HighFrequencyTick.get()),
        HIGH_CLOSE(40, HighFrequencyTick.get()),
        NORMAL_CLOSE(80, NormalFrequencyTick.get()),
        LOW_CLOSE(150, LowFrequencyTick.get());

        private final double distance;
        private static final Closeness[] order = new Closeness[]{HIGH_CHARGE_CLOSE, HIGH_CLOSE, NORMAL_CLOSE, LOW_CLOSE};
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
        OVERSHOOT_DISTANCE("overshoot_distance", 10),
        OVERSHOOT_SPEED("charge_speed", 2.0d),
        TOO_CLOSE_TO_CHARGE("too_close_to_charge", 4d),
        MARGIN_OF_ERROR("margin_of_error_in_charge_choice", 2.5),
        MAX_CHARGE_TIME("charge_exit_failsafe", 20 * 5),
        CHARGE_CHANCE("charge_chance", 0.02),
        CHARGE_COOLDOWN("charge_cooldown", 90),
        CHARGE_UP_TIME("charge_up_time", 20),
        CHARGE_STUN_TIME("charge_stun_time", 100),
        CHARGE_TIRED_TIME("charge_tired_time", 50);

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
