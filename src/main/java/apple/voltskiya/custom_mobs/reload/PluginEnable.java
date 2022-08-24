package apple.voltskiya.custom_mobs.reload;

import apple.lib.pmc.PluginModule;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import voltskiya.apple.utilities.minecraft.TagConstants;

public class PluginEnable extends PluginModule {

    @Override
    public void enable() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> {
            for (World world : Bukkit.getWorlds()) {
                for (Entity entity : world.getEntities()) {
                    entity.removeScoreboardTag(TagConstants.IS_DOING_ABILITY);
                }
            }
        }, 160);
    }

    @Override
    public String getName() {
        return "enable_shared";
    }
}
