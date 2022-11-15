package apple.voltskiya.custom_mobs.abilities.tick.orbital_strike.large;

import apple.mc.utilities.world.vector.VectorUtils;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;
import apple.voltskiya.custom_mobs.util.ticking.LowFrequencyTick;
import apple.voltskiya.custom_mobs.util.ticking.TickGiverable;
import apple.voltskiya.custom_mobs.util.ticking.VeryLowFrequencyTick;
import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class LargeOrbitalStrikeManagerTicker implements SpawnListener {

    private static LargeOrbitalStrikeManagerTicker instance;
    private final Map<Closeness, LargeOrbitalStrikeIndividualTicker> closenessToStrikeres = new HashMap<>() {{
        for (Closeness closeness : Closeness.values())
            put(closeness, new LargeOrbitalStrikeIndividualTicker(closeness));
    }};

    public LargeOrbitalStrikeManagerTicker() throws IOException {
        instance = this;
        closenessToStrikeres.get(Closeness.HIGH_CLOSE).setIsCheckStrike();
    }

    public static LargeOrbitalStrikeManagerTicker get() {
        return instance;
    }


    @Override
    public void doSpawn(MMSpawned mmSpawned) {
        Entity striker = mmSpawned.getEntity();
        // this is a striker
        Closeness closeness = determineConcern(striker);
        closenessToStrikeres.get(closeness).giveStriker(striker, 0);
    }


    @Override
    public String getBriefTag() {
        return "large_orbital_strike";
    }


    public boolean amIGivingStriker(Entity entity, Closeness currentCloseness, long lastStrike) {
        Closeness actualCloseness = determineConcern(entity);
        if (actualCloseness != currentCloseness) {
            closenessToStrikeres.get(actualCloseness).giveStriker(entity, lastStrike);
            return true;
        }
        return false;
    }

    private Closeness determineConcern(Entity striker) {
        Location strikerLocation = striker.getLocation();

        @Nullable Player player = UpdatedPlayerList.getClosestPlayerPlayer(strikerLocation);
        if (player == null)
            return Closeness.lowest();
        else
            return Closeness.getCloseness(strikerLocation, player.getLocation());
    }

    enum Closeness {
        HIGH_CLOSE(100, LowFrequencyTick.get()),
        NORMAL_CLOSE(200, VeryLowFrequencyTick.get());

        private final double distance;
        private static final Closeness[] order = new Closeness[]{HIGH_CLOSE, NORMAL_CLOSE};
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
