package apple.voltskiya.custom_mobs.mobs.abilities.tick.parent;

import apple.mc.utilities.world.vector.VectorUtils;
import apple.voltskiya.custom_mobs.util.PlayerClose;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;
import apple.voltskiya.custom_mobs.util.ticking.HighFrequencyTick;
import apple.voltskiya.mob_manager.listen.SpawnHandlerListener;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

public abstract class MobTickManagerTicker<Config extends MobTickerConfig>
    implements Runnable, SpawnHandlerListener {

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
    public String getTag() {
        return config.getTag();
    }

    @Override
    public String getName() {
        return config.getTag();
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

    @Override
    public void handle(MMSpawned mmSpawned) {
        eatEntity(mmSpawned.getEntity());
        eatMMSpawned(mmSpawned);
    }

    public void eatEntity(Entity entity) {
        addMobToTick(config.createMob(entity));
    }

    public void eatMMSpawned(MMSpawned mmSpawned) {
    }

    public void addMobToTick(MobToTick<?> mob) {
        Location mobLocation = mob.getBukkitEntity().getLocation();
        PlayerClose closestPlayer = UpdatedPlayerList.getClosestPlayer(mobLocation);
        MobTickCloseness closeness = getCloseness(mobLocation, closestPlayer.getLocation());
        this.closenessToTicker[closeness.ordinal()].addMobToTick(mob);
    }

    private MobTickCloseness getCloseness(Location aLocation, @Nullable Location bLocation) {
        double d = VectorUtils.distance(aLocation, bLocation);
        for (MobTickCloseness closeness : closeness) {
            if (closeness.getDistance() >= d) {
                return closeness;
            }
        }
        return closeness[closeness.length - 1];
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
                for (Iterator<MobToTick<?>> iterator = mobsToTick.values().iterator();
                    iterator.hasNext(); ) {
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
