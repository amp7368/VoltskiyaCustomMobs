package apple.voltskiya.custom_mobs.abilities.tick.charger;

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
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChargerManagerTicker extends SpawnEater {
    private static ChargerManagerTicker instance;
    private final Map<Closeness, ChargerIndividualTicker> closenessToChargeres = new HashMap<>() {{
        for (Closeness closeness : Closeness.values())
            put(closeness, new ChargerIndividualTicker(closeness.getGiver(), closeness));
        get(Closeness.HIGH_CLOSE).setChargeTick();
    }};

    public ChargerManagerTicker() throws IOException {
        instance = this;
        ChargerType.NORMAL.set(
                (int) getValueOrInit(YmlSettings.NORMAL_OVERSHOOT_DISTANCE.getPath()),
                (double) getValueOrInit(YmlSettings.NORMAL_OVERSHOOT_SPEED.getPath()),
                (double) getValueOrInit(YmlSettings.NORMAL_TOO_CLOSE_TO_CHARGE.getPath()),
                (double) getValueOrInit(YmlSettings.NORMAL_MARGIN_OF_ERROR.getPath()),
                (int) getValueOrInit(YmlSettings.NORMAL_MAX_CHARGE_TIME.getPath()),
                (double) getValueOrInit(YmlSettings.NORMAL_CHARGE_CHANCE.getPath()),
                ((int) getValueOrInit(YmlSettings.NORMAL_CHARGE_COOLDOWN.getPath())),
                ((int) getValueOrInit(YmlSettings.NORMAL_CHARGE_UP_TIME.getPath())),
                ((int) getValueOrInit(YmlSettings.NORMAL_CHARGE_STUN_TIME.getPath())),
                ((int) getValueOrInit(YmlSettings.NORMAL_CHARGE_TIRED_TIME.getPath()))
        );
        ChargerType.QUICK.set(
                (int) getValueOrInit(YmlSettings.QUICK_OVERSHOOT_DISTANCE.getPath()),
                (double) getValueOrInit(YmlSettings.QUICK_OVERSHOOT_SPEED.getPath()),
                (double) getValueOrInit(YmlSettings.QUICK_TOO_CLOSE_TO_CHARGE.getPath()),
                (double) getValueOrInit(YmlSettings.QUICK_MARGIN_OF_ERROR.getPath()),
                (int) getValueOrInit(YmlSettings.QUICK_MAX_CHARGE_TIME.getPath()),
                (double) getValueOrInit(YmlSettings.QUICK_CHARGE_CHANCE.getPath()),
                ((int) getValueOrInit(YmlSettings.QUICK_CHARGE_COOLDOWN.getPath())),
                ((int) getValueOrInit(YmlSettings.QUICK_CHARGE_UP_TIME.getPath())),
                ((int) getValueOrInit(YmlSettings.QUICK_CHARGE_STUN_TIME.getPath())),
                ((int) getValueOrInit(YmlSettings.QUICK_CHARGE_TIRED_TIME.getPath()))
        );
        for (UUID mob : getMobs()) {
            @Nullable Entity striker = Bukkit.getEntity(mob);
            if (!(striker instanceof Mob)) continue;
            Closeness closeness = determineConcern((Mob) striker);
            for (ChargerType type : ChargerType.values()) {
                if (striker.getScoreboardTags().contains(type.getTag())) {
                    closenessToChargeres.get(closeness).giveCharger(new Charger((Mob) striker, type));
                }
            }
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
            for (ChargerType type : ChargerType.values()) {
                if (charger.getScoreboardTags().contains(type.getTag())) {
                    closenessToChargeres.get(closeness).giveCharger(new Charger(charger, type));
                }
            }
            addMobs(charger.getUniqueId());
        }
    }

    public void giveCharger(Charger charger) {
        Closeness closeness = determineConcern(charger.getEntity());
        for (ChargerType type : ChargerType.values()) {
            if (charger.getEntity().getScoreboardTags().contains(type.getTag())) {
                closenessToChargeres.get(closeness).giveCharger(charger);
            }
        }
    }

    @Override
    public String getName() {
        return "charger";
    }

    @Override
    public apple.voltskiya.custom_mobs.YmlSettings[] getSettings() {
        return YmlSettings.values();
    }

    @Override
    public void initializeYml() throws IOException {
        for (YmlSettings setting : YmlSettings.values())
            setValueIfNotExists(setting.getPath(), setting.getValue());
    }

    @Override
    protected VoltskiyaModule getPlugin() {
        return MobTickPlugin.get();
    }


    public boolean amIGivingCharger(Charger charger, Closeness currentCloseness) {
        Closeness actualCloseness = determineConcern(charger.getEntity());
        if (actualCloseness != currentCloseness) {
            closenessToChargeres.get(actualCloseness).giveCharger(charger);
            return true;
        }
        return false;
    }

    private Closeness determineConcern(Mob charger) {
        Location chargerLocation = charger.getLocation();

        @Nullable Player player = UpdatedPlayerList.getClosestPlayer(chargerLocation);
        if (player == null)
            return Closeness.lowest();
        else
            return Closeness.getCloseness(chargerLocation, player.getLocation());
    }

    enum Closeness {
        HIGH_CLOSE(40, HighFrequencyTick.get()),
        NORMAL_CLOSE(80, NormalFrequencyTick.get()),
        LOW_CLOSE(150, LowFrequencyTick.get());

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
        NORMAL_OVERSHOOT_DISTANCE("normal.overshoot_distance", 10),
        NORMAL_OVERSHOOT_SPEED("normal.charge_speed", 2.0d),
        NORMAL_TOO_CLOSE_TO_CHARGE("normal.too_close_to_charge", 4d),
        NORMAL_MARGIN_OF_ERROR("normal.margin_of_error_in_charge_choice", 2.5),
        NORMAL_MAX_CHARGE_TIME("normal.charge_exit_failsafe", 20 * 5),
        NORMAL_CHARGE_CHANCE("normal.charge_chance", 0.02),
        NORMAL_CHARGE_COOLDOWN("normal.charge_cooldown", 90),
        NORMAL_CHARGE_UP_TIME("normal.charge_up_time", 20),
        NORMAL_CHARGE_STUN_TIME("normal.charge_stun_time", 100),
        NORMAL_CHARGE_TIRED_TIME("normal.charge_tired_time", 50),
        QUICK_OVERSHOOT_DISTANCE("quick.overshoot_distance", 10),
        QUICK_OVERSHOOT_SPEED("quick.charge_speed", 2.0d),
        QUICK_TOO_CLOSE_TO_CHARGE("quick.too_close_to_charge", 4d),
        QUICK_MARGIN_OF_ERROR("quick.margin_of_error_in_charge_choice", 2.5),
        QUICK_MAX_CHARGE_TIME("quick.charge_exit_failsafe", 20 * 5),
        QUICK_CHARGE_CHANCE("quick.charge_chance", 0.02),
        QUICK_CHARGE_COOLDOWN("quick.charge_cooldown", 90),
        QUICK_CHARGE_UP_TIME("quick.charge_up_time", 20),
        QUICK_CHARGE_STUN_TIME("quick.charge_stun_time", 100),
        QUICK_CHARGE_TIRED_TIME("quick.charge_tired_time", 50);

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
