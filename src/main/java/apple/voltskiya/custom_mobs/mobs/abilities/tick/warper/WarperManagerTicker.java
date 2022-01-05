package apple.voltskiya.custom_mobs.mobs.abilities.tick.warper;

import apple.voltskiya.custom_mobs.mobs.abilities.MobTickPlugin;
import apple.voltskiya.custom_mobs.mobs.nms.parent.config.ConfigManager;
import apple.voltskiya.custom_mobs.mobs.nms.parent.register.RegisteredEntityEater;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;
import apple.voltskiya.custom_mobs.util.ticking.HighFrequencyTick;
import apple.voltskiya.custom_mobs.util.ticking.LowFrequencyTick;
import apple.voltskiya.custom_mobs.util.ticking.NormalFrequencyTick;
import apple.voltskiya.custom_mobs.util.ticking.TickGiverable;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;
import voltskiya.apple.utilities.util.DistanceUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WarperManagerTicker extends ConfigManager implements RegisteredEntityEater {
    public int PARTICLES;
    public int WARP_RADIUS;
    public double WARP_CHANCE;
    private final Map<WarperManagerTicker.Closeness, WarperIndividualTicker> closenessToWarperes = new HashMap<>() {{
        for (WarperManagerTicker.Closeness closeness : WarperManagerTicker.Closeness.values())
            put(closeness, new WarperIndividualTicker(closeness));
        get(Closeness.HIGH_CLOSE).setIsWarping();
    }};
    private static WarperManagerTicker instance;

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
    public void eatEntity(Entity warper) {
        // this is a warper
        WarperManagerTicker.Closeness closeness = determineConcern(warper);
        closenessToWarperes.get(closeness).giveWarper(warper);
    }

    @Override
    public String getName() {
        return "warper";
    }

    @Override
    public apple.voltskiya.custom_mobs.mobs.nms.parent.config.YmlSettings[] getSettings() {
        return YmlSettings.values();
    }

    @Override
    public void initializeYml() throws IOException {
        for (WarperManagerTicker.YmlSettings setting : WarperManagerTicker.YmlSettings.values()) {
            setValueIfNotExists(setting.getPath(), setting.value);
        }
    }

    @Override
    protected PluginManagedModule getPlugin() {
        return MobTickPlugin.get();
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

        @Nullable Player player = UpdatedPlayerList.getClosestPlayerPlayer(warperLocation);
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

    private enum YmlSettings implements apple.voltskiya.custom_mobs.mobs.nms.parent.config.YmlSettings {
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

