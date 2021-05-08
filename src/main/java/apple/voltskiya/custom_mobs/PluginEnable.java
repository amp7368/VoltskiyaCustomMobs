package apple.voltskiya.custom_mobs;

import apple.voltskiya.custom_mobs.util.constants.TagConstants;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class PluginEnable extends VoltskiyaModule {
    @Override
    public void enable() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                entity.removeScoreboardTag(TagConstants.isDoingAbility);
            }
        }
    }

    @Override
    public String getName() {
        return "enable_shared";
    }
}
