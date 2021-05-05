package apple.voltskiya.custom_mobs.ticking;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.abilities.tick.Tickable;
import org.bukkit.Bukkit;

public class Ticking extends VoltskiyaModule {
    private Tickable[] tickables;
    private Ticking instance;


    @Override
    public void init() {
        instance = this;
    }

    public void tick() {
        for (Tickable tickable : tickables) {
            tickable.tick();
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this::tick, 1);
    }

    @Override
    public void enable() {
        tickables = new Tickable[]{
                new HighFrequencyTick(),
                new NormalHighFrequencyTick(),
                new NormalFrequencyTick(),
                new LowFrequencyTick(),
                new VeryLowFrequencyTick(),
        };
        tick();
    }

    @Override
    public String getName() {
        return "Ticking";
    }
}
