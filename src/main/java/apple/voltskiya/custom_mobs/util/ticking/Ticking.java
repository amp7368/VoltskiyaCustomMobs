package apple.voltskiya.custom_mobs.util.ticking;

import apple.lib.pmc.PluginModule;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.Tickable;
import org.bukkit.Bukkit;

public class Ticking extends PluginModule {

    private Tickable[] tickables;

    public void tick() {
        for (Tickable tickable : tickables) {
            tickable.tick();
        }
    }

    @Override
    public void enable() {
        tickables = new Tickable[]{new HighFrequencyTick(), new NormalHighFrequencyTick(),
            new NormalFrequencyTick(), new LowFrequencyTick(), new VeryLowFrequencyTick(),};
        Bukkit.getScheduler().scheduleSyncRepeatingTask(VoltskiyaPlugin.get(), this::tick, 1, 1);
    }

    @Override
    public String getName() {
        return "Ticking";
    }
}