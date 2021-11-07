package apple.voltskiya.custom_mobs.mobs.abilities.tick.lost_soul;

import apple.voltskiya.custom_mobs.mobs.ConfigManager;
import apple.voltskiya.custom_mobs.mobs.RegisteredEntityEater;
import apple.voltskiya.custom_mobs.mobs.abilities.MobTickPlugin;
import apple.voltskiya.custom_mobs.ticking.HighFrequencyTick;
import apple.voltskiya.custom_mobs.ticking.LowFrequencyTick;
import apple.voltskiya.custom_mobs.ticking.NormalFrequencyTick;
import apple.voltskiya.custom_mobs.ticking.TickGiverable;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;
import org.bukkit.Location;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vex;
import org.jetbrains.annotations.Nullable;
import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;
import voltskiya.apple.utilities.util.DistanceUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LostSoulManagerTicker extends ConfigManager implements RegisteredEntityEater {
    protected final double DAMAGE_AMOUNT;
    private static LostSoulManagerTicker instance;
    private final Map<Closeness, LostSoulIndividualTicker> closenessToVexes = new HashMap<>() {{
        for (Closeness closeness : Closeness.values())
            put(closeness, new LostSoulIndividualTicker(closeness.getGiver(), closeness));
    }};

    public LostSoulManagerTicker() throws IOException {
        instance = this;
        closenessToVexes.get(Closeness.HIGH_CLOSE).setIsCheckCollision();
        DAMAGE_AMOUNT = (double) getValueOrInit(YmlSettings.DAMAGE_AMOUNT.getPath());
    }

    public static LostSoulManagerTicker get() {
        return instance;
    }

    @Override
    public void eatEntity(Mob entity) {
        if (entity instanceof Vex) {
            // this is a vex
            final Vex vex = (Vex) entity;
            Closeness closeness = determineConcern(vex);
            closenessToVexes.get(closeness).giveVex(vex);
            addMob(vex.getUniqueId());
        }
    }

    @Override
    public String getName() {
        return "lost_soul";
    }

    @Override
    public apple.voltskiya.custom_mobs.mobs.YmlSettings[] getSettings() {
        return YmlSettings.values();
    }

    @Override
    public void initializeYml() throws IOException {
        setValueIfNotExists("damageAmount", 2d);
    }

    @Override
    protected PluginManagedModule getPlugin() {
        return MobTickPlugin.get();
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

    private enum YmlSettings implements apple.voltskiya.custom_mobs.mobs.YmlSettings {
        DAMAGE_AMOUNT("damageAmount", 2d);

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
