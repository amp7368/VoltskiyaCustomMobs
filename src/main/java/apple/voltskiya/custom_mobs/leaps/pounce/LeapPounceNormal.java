package apple.voltskiya.custom_mobs.leaps.pounce;

import apple.voltskiya.custom_mobs.leaps.LeapEater;
import apple.voltskiya.custom_mobs.leaps.LeapPlugin;
import apple.voltskiya.custom_mobs.mobs.nms.parent.config.ConfigManager;
import apple.voltskiya.custom_mobs.mobs.nms.parent.config.YmlSettings;
import net.minecraft.world.entity.EntityInsentient;
import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;

public class LeapPounceNormal extends ConfigManager implements LeapEater {

    public void eatEntity(EntityInsentient entity) {
        LeapPounce.eatEntity(entity, getConfig());
    }

    /**
     * @return the name of the sub_module (a step below a module)
     */
    @Override
    public String getName() {
        return "pounce_leap";
    }

    /**
     * @return the default values for the config file
     */
    @Override
    public YmlSettings[] getSettings() {
        return new YmlSettings[0];
    }

    /**
     * @return the module associated with this config
     */
    @Override
    protected PluginManagedModule getPlugin() {
        return LeapPlugin.get();
    }
}
