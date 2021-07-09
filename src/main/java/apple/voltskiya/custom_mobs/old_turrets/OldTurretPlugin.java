package apple.voltskiya.custom_mobs.old_turrets;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.old_turrets.gui.OldTurretGuiManager;
import org.bukkit.Bukkit;

public class OldTurretPlugin extends VoltskiyaModule {
    private static OldTurretPlugin instance;

    @Override
    public void init() {
        instance = this;
    }

    public static OldTurretPlugin get() {
        return instance;
    }

    @Override
    public void enable() {
        new OldTurretCommand();
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
            new OldTurretManagerTicker();
            new OldTurretGuiManager();
        }, 0);
    }

    @Override
    public String getName() {
        return "T";
    }
}
