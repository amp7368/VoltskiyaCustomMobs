package apple.voltskiya.custom_mobs.ticking;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.Tickable;
import org.bukkit.Bukkit;
import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;

public class Ticking extends PluginManagedModule {
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
        Bukkit.getScheduler().scheduleSyncRepeatingTask(VoltskiyaPlugin.get(), this::tick, 1, 1);
    }

    @Override
    public String getName() {
        return "Ticking";
    }
}
