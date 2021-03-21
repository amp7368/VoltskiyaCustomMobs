package apple.voltskiya.custom_mobs.ticking;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.mob_tick.MobTickPlugin;
import apple.voltskiya.custom_mobs.mob_tick.tick.Tickable;
import org.bukkit.Bukkit;

public class Ticking extends VoltskiyaModule {
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
        tick();
    }

    @Override
    public String getName() {
        return null;
    }
}
