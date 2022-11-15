package apple.voltskiya.custom_mobs.reload;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import com.voltskiya.lib.AbstractModule;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import voltskiya.apple.utilities.minecraft.TagConstants;

public class EnableModule extends AbstractModule {

    @Override
    public void enable() {
        VoltskiyaPlugin.get().scheduleSyncDelayedTask(this::removeIsDoingAbility);
    }

    private void removeIsDoingAbility() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                entity.removeScoreboardTag(TagConstants.IS_DOING_ABILITY);
            }
        }
    }

    @Override
    public String getName() {
        return "enable_shared";
    }
}
