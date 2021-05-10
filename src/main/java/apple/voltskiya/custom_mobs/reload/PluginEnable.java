package apple.voltskiya.custom_mobs.reload;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.util.constants.TagConstants;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class PluginEnable extends VoltskiyaModule {
    @Override
    public void enable() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
            for (World world : Bukkit.getWorlds()) {
                for (Entity entity : world.getEntities()) {
                    entity.removeScoreboardTag(TagConstants.isDoingAbility);
                }
            }
        }, 160);
    }

    @Override
    public String getName() {
        return "enable_shared";
    }
}
