package apple.voltskiya.custom_mobs.mobs.abilities.tick.parent;

import apple.voltskiya.custom_mobs.mobs.RegisteredEntityEater;
import apple.voltskiya.custom_mobs.mobs.abilities.MobTickPlugin;
import apple.voltskiya.custom_mobs.ticking.HighFrequencyTick;
import apple.voltskiya.custom_mobs.util.PlayerClose;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.configs.plugin.manage.PluginManagedModuleConfig;
import voltskiya.apple.utilities.util.DistanceUtils;

import java.util.*;

public abstract class MobTickManagerTicker<Config extends MobConfig>
        implements Runnable,
        RegisteredEntityEater {
    protected final Config config;
    private final MobTickCloseness[] closeness;
    private final MobTicker[] closenessToTicker;

    public MobTickManagerTicker(Config config) {
        this.config = config;
        closeness = getClosenessOrdered();
        this.closenessToTicker = new MobTicker[closeness.length];
        for (MobTickCloseness close : closeness) {
            closenessToTicker[close.ordinal()] = new MobTicker(close);
        }
        HighFrequencyTick.get().add(this);
    }

    @Override
    public void run() {
        for (MobTicker ticker : closenessToTicker) {
            ticker.tick();
        }
    }

    private MobTickCloseness[] getClosenessOrdered() {
        return Arrays.stream(getClosenessValues())
                .sorted(Comparator.comparingDouble(MobTickCloseness::getDistance))
                .toArray(MobTickCloseness[]::new);
    }

    protected MobTickCloseness[] getClosenessValues() {
        return MobTickManagerClosenessDefault.values();
    }

    public void eatEntity(Entity entity) {
        addMobToTick(config.createMob(entity));
    }

    public void addMobToTick(MobToTick<?> mob) {
        Location mobLocation = mob.getBukkitEntity().getLocation();
        PlayerClose closestPlayer = UpdatedPlayerList.getClosestPlayer(mobLocation);
        MobTickCloseness closeness = getCloseness(mobLocation, closestPlayer.getLocation());
        this.closenessToTicker[closeness.ordinal()].addMobToTick(mob);
    }

    private MobTickCloseness getCloseness(Location aLocation, @Nullable Location bLocation) {
        double d = DistanceUtils.distance(aLocation, bLocation);
        for (MobTickCloseness closeness : closeness) {
            if (closeness.getDistance() >= d) {
                return closeness;
            }
        }
        return closeness[closeness.length - 1];
    }

    public PluginManagedModuleConfig getModule() {
        return MobTickPlugin.get();
    }

    public boolean shouldAccept(LivingEntity entity) {
        return !isOnlyMobs() || entity instanceof Mob;
    }

    protected boolean isOnlyMobs() {
        return false;
    }

    public static class MobTicker {
        private final int tickSpeed;
        private final Map<UUID, MobToTick<?>> mobsToTick = new HashMap<>();
        private int currentTick;

        public MobTicker(MobTickCloseness closeness) {
            this.tickSpeed = closeness.getTicksPerRun();
            this.currentTick = 0;
        }

        public void addMobToTick(MobToTick<?> mob) {
            this.mobsToTick.put(mob.getBukkitEntity().getUniqueId(), mob);
        }

        public void removeMobFromTick(MobToTick<?> mob) {
            this.mobsToTick.remove(mob.getBukkitEntity().getUniqueId());
        }

        public void tick() {
            if (++currentTick % tickSpeed == 0) {
                currentTick = 0;
                doTickOnMobs();
            }
        }

        private void doTickOnMobs() {
            synchronized (mobsToTick) {
                for (Iterator<MobToTick<?>> iterator = mobsToTick.values().iterator(); iterator.hasNext(); ) {
                    MobToTick<?> mobToTick = iterator.next();
                    if (mobToTick.shouldRemove()) {
                        mobToTick.killIfNotDead();
                        iterator.remove();
                        continue;
                    }
                    mobToTick.tick_(tickSpeed);
                }
            }
        }
    }
}
