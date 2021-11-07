package apple.voltskiya.custom_mobs.trash.old_turrets;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.trash.old_turrets.gui.OldTurretGuiManager;
import org.bukkit.Bukkit;
import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;

public class OldTurretPlugin extends PluginManagedModule {
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
