package apple.voltskiya.custom_mobs.abilities.common.sweb;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class SWebInit {

    public static void initialize() {
        // remove all old spider webs
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getScoreboardTags().contains(SWebConfig.SWEB_WEB_TAG)) {
                    entity.remove();
                }
            }
        }
    }
}
