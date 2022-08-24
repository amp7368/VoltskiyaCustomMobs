package apple.voltskiya.custom_mobs.mobs.abilities.tick.charger;

import apple.mc.utilities.world.vector.VectorUtils;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;
import apple.voltskiya.custom_mobs.util.ticking.HighFrequencyTick;
import apple.voltskiya.custom_mobs.util.ticking.LowFrequencyTick;
import apple.voltskiya.custom_mobs.util.ticking.NormalFrequencyTick;
import apple.voltskiya.custom_mobs.util.ticking.TickGiverable;
import apple.voltskiya.mob_manager.listen.SpawnHandlerListener;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChargerManagerTicker implements SpawnHandlerListener {

    private static ChargerManagerTicker instance;
    private final Map<Closeness, ChargerIndividualTicker> closenessToChargeres = new HashMap<>() {{
        for (Closeness closeness : Closeness.values())
            put(closeness, new ChargerIndividualTicker(closeness.getGiver(), closeness));
        get(Closeness.HIGH_CLOSE).setChargeTick();
    }};

    public ChargerManagerTicker() {
        instance = this;
    }

    public static ChargerManagerTicker get() {
        return instance;
    }

    @Override
    public boolean isOnlyMobs() {
        return true;
    }

    @Override
    public void handle(MMSpawned mmSpawned) {
        @NotNull Mob charger = mmSpawned.getMob();
        Closeness closeness = determineConcern(charger);
        for (ChargerType type : ChargerType.values()) {
            if (charger.getScoreboardTags().contains(type.getTag())) {
                closenessToChargeres.get(closeness).giveCharger(new Charger(charger, type));
            }
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
    public String getTag() {
        return "charger";
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

        @Nullable Player player = UpdatedPlayerList.getClosestPlayerPlayer(chargerLocation);
        if (player == null)
            return Closeness.lowest();
        else
            return Closeness.getCloseness(chargerLocation, player.getLocation());
    }


    enum Closeness {
        HIGH_CLOSE(60, HighFrequencyTick.get()),
        NORMAL_CLOSE(80, NormalFrequencyTick.get()),
        LOW_CLOSE(150, LowFrequencyTick.get());

        private final double distance;
        private static final Closeness[] order = new Closeness[]{HIGH_CLOSE, NORMAL_CLOSE,
            LOW_CLOSE};
        private final TickGiverable giver;

        Closeness(double distance, TickGiverable giver) {
            this.distance = distance;
            this.giver = giver;
        }

        private static Closeness getCloseness(Location aLocation, Location bLocation) {
            double d = VectorUtils.distance(aLocation, bLocation);
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

}
