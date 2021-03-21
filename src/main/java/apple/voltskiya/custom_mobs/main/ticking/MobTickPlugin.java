package apple.voltskiya.custom_mobs.main.ticking;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.main.UpdatedPlayerList;
import apple.voltskiya.custom_mobs.tick.MobListSql;
import apple.voltskiya.custom_mobs.main.listeners.MobDeathListener;
import apple.voltskiya.custom_mobs.main.listeners.MobSpawnListener;
import apple.voltskiya.custom_mobs.tick.Tickable;
import org.bukkit.Bukkit;

public class MobTickPlugin extends VoltskiyaModule {

    private Tickable[] tickables;
    private static MobTickPlugin instance;

    public static MobTickPlugin get() {
        return instance;
    }

    public void tick() {
        for (Tickable tickable : tickables) {
            tickable.tick();
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this::tick, 1);
    }

    @Override
    public void enable() {
        instance = this;
        MobListSql.initialize();
        tickables = new Tickable[]{
                new HighFrequencyTick(),
                new NormalFrequencyTick(),
                new LowFrequencyTick(),
                new VeryLowFrequencyTick()
        };
        new UpdatedPlayerList();
        new MobDeathListener();
        new MobSpawnListener();
        tick();
    }

    @Override
    public String getName() {
        return "MobTick";
    }
}
