package apple.voltskiya.custom_mobs.turrets;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.sql.VerifyTurretsSql;
import apple.voltskiya.custom_mobs.turrets.gui.TurretGuiManager;
import org.bukkit.Bukkit;

public class TurretPlugin extends VoltskiyaModule {
    private static TurretPlugin instance;

    @Override
    public void enable() {
        instance = this;
        VerifyTurretsSql.initialize();
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
