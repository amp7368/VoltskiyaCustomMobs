package apple.voltskiya.custom_mobs.heartbeat;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.heartbeat.tick.listeners.MobSpawnListener;
import apple.voltskiya.custom_mobs.heartbeat.tick.main.HighFrequencyTick;
import apple.voltskiya.custom_mobs.heartbeat.tick.main.LowFrequencyTick;
import apple.voltskiya.custom_mobs.heartbeat.tick.main.NormalFrequencyTick;
import apple.voltskiya.custom_mobs.heartbeat.tick.Tickable;
import apple.voltskiya.custom_mobs.heartbeat.tick.main.VeryLowFrequencyTick;
import org.bukkit.Bukkit;

public class MobTick extends VoltskiyaModule {

    private Tickable[] tickables;


    public void tick() {
        for (Tickable tickable : tickables) {
            tickable.tick();
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this::tick, 1);
    }

    @Override
    public void enable() {
        System.out.println("enabled");
        tickables = new Tickable[]{
                new HighFrequencyTick(),
                new NormalFrequencyTick(),
                new LowFrequencyTick(),
                new VeryLowFrequencyTick()
        };
        new MobSpawnListener();
        tick();
    }

    @Override
    public String getName() {
        return "MobTick";
    }
}
