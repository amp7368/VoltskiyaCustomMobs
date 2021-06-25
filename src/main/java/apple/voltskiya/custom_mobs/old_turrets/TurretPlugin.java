package apple.voltskiya.custom_mobs.old_turrets;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.old_turrets.gui.TurretGuiManager;
import org.bukkit.Bukkit;

public class TurretPlugin extends VoltskiyaModule {
    private static TurretPlugin instance;

    @Override
    public void init() {
        instance = this;
    }

    @Override
    public void enable() {
        new TurretCommand();
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
            new TurretManagerTicker();
            new TurretGuiManager();
        }, 0);
    }

    @Override
    public String getName() {
        return "Turret";
    }

    public static TurretPlugin get() {
        return instance;
    }
}
