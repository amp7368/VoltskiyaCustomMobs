package apple.voltskiya.custom_mobs.abilities.nether.lost_soul;

import apple.mc.utilities.world.vector.VectorUtils;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;
import apple.voltskiya.custom_mobs.util.ticking.HighFrequencyTick;
import apple.voltskiya.custom_mobs.util.ticking.LowFrequencyTick;
import apple.voltskiya.custom_mobs.util.ticking.NormalFrequencyTick;
import apple.voltskiya.custom_mobs.util.ticking.TickGiverable;
import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vex;
import org.jetbrains.annotations.Nullable;

public class LostSoulManagerTicker implements SpawnListener {

    protected final double DAMAGE_AMOUNT = 2;
    private static LostSoulManagerTicker instance;
    private final Map<Closeness, LostSoulIndividualTicker> closenessToVexes = new HashMap<>() {{
        for (Closeness closeness : Closeness.values())
            put(closeness, new LostSoulIndividualTicker(closeness.getGiver(), closeness));
    }};

    public LostSoulManagerTicker() {
        instance = this;
        this.registerSpawnListener();
        closenessToVexes.get(Closeness.HIGH_CLOSE).setIsCheckCollision();
    }

    public static LostSoulManagerTicker get() {
        return instance;
    }

    @Override
    public boolean isOnlyMobs() {
        return true;
    }

    @Override
    public void doSpawn(MMSpawned mmSpawned) {
        Entity entity = mmSpawned.getEntity();
        if (entity instanceof Vex vex) {
            Closeness closeness = determineConcern(vex);
            closenessToVexes.get(closeness).giveVex(vex);
        }
    }

    @Override
    public String getBriefTag() {
        return "lost_soul";
    }


    public boolean amIGivingVex(Vex vex, Closeness currentCloseness) {
        Closeness actualCloseness = determineConcern(vex);
        if (actualCloseness != currentCloseness) {
            closenessToVexes.get(actualCloseness).giveVex(vex);
            return true;
        }
        return false;
    }

    private Closeness determineConcern(Vex vex) {
        Location vexLocation = vex.getLocation();

        @Nullable Player player = UpdatedPlayerList.getClosestPlayerPlayer(vexLocation);
        if (player == null)
            return Closeness.lowest();
        else
            return Closeness.getCloseness(vexLocation, player.getLocation());
    }

    enum Closeness {
        HIGH_CLOSE(30, HighFrequencyTick.get()),
        NORMAL_CLOSE(70, NormalFrequencyTick.get()),
        LOW_CLOSE(200, LowFrequencyTick.get());

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
