package apple.voltskiya.custom_mobs.turret.manage;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobTickCloseness;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobTickManagerClosenessDefault;
import apple.voltskiya.custom_mobs.turret.PluginTurret;
import apple.voltskiya.custom_mobs.turret.manage.model.config.TurretModelConfig;
import apple.voltskiya.custom_mobs.turret.manage.model.create.TurretMobConstructor;
import apple.voltskiya.custom_mobs.turret.manage.model.create.TurretModelConstructor;
import apple.voltskiya.custom_mobs.turret.manage.model.impl.TurretModelImpl;
import apple.voltskiya.custom_mobs.turret.parent.TurretMob;
import apple.voltskiya.custom_mobs.util.PlayerClose;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;
import apple.voltskiya.custom_mobs.util.ticking.HighFrequencyTick;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.configs.plugin.manage.PluginManagedModuleConfig;
import voltskiya.apple.utilities.util.DistanceUtils;

import java.util.*;

public class TurretTickManager
        implements Runnable {
    private final MobTickCloseness[] closeness;
    private final TurretTicker[] closenessToTicker;
    private final TurretModelConfig model;
    private final TurretModelConstructor createModel;
    private final TurretMobConstructor<?> constructor;

    public TurretTickManager(TurretModelConfig model, TurretModelConstructor createModel, TurretMobConstructor<?> constructor) {
        this.model = model;
        this.createModel = createModel;
        this.constructor = constructor;
        closeness = getClosenessOrdered();
        this.closenessToTicker = new TurretTicker[closeness.length];
        for (MobTickCloseness close : closeness) {
            closenessToTicker[close.ordinal()] = new TurretTicker(close);
        }
        HighFrequencyTick.get().add(this);
    }

    @Override
    public void run() {
        for (TurretTicker ticker : closenessToTicker) {
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

    public void spawnNew(Location location) {
        TurretModelImpl model = createModel.create(this.model, location);
        model.spawn();
        TurretMob<?> mob = constructor.create(model);
        mob.initialize();
    }

    public void addMobToTick(TurretMob<?> mob) {
        Location mobLocation = mob.getLocation();
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
        return PluginTurret.get();
    }

    public static class TurretTicker {
        private final int tickSpeed;
        private final Map<UUID, TurretMob<?>> mobsToTick = new HashMap<>();
        private int currentTick;

        public TurretTicker(MobTickCloseness closeness) {
            this.tickSpeed = closeness.getTicksPerRun();
            this.currentTick = 0;
        }

        public void addMobToTick(TurretMob<?> mob) {
            this.mobsToTick.put(mob.getUniqueId(), mob);
        }

        public void removeMobFromTick(TurretMob<?> mob) {
            this.mobsToTick.remove(mob.getUniqueId());
        }

        public void tick() {
            if (++currentTick % tickSpeed == 0) {
                currentTick = 0;
                doTickOnMobs();
            }
        }

        private void doTickOnMobs() {
            synchronized (mobsToTick) {
                for (Iterator<TurretMob<?>> iterator = mobsToTick.values().iterator(); iterator.hasNext(); ) {
                    TurretMob<?> mobToTick = iterator.next();
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
