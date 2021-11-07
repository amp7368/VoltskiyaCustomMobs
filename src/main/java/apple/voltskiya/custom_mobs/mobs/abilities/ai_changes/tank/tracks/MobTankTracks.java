package apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.tank.tracks;

import apple.voltskiya.custom_mobs.mobs.ConfigManager;
import apple.voltskiya.custom_mobs.mobs.RegisteredEntityEater;
import apple.voltskiya.custom_mobs.mobs.YmlSettings;
import apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.flamethrower.FlameThrowerManager;
import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;

import java.util.Map;

public class MobTankTracks extends ConfigManager implements RegisteredEntityEater {
    public Map<String, FlameThrowerManager.FlamethrowerType> tagToFlamethrowerType;

    @Override
    public String getName() {
        return null;
    }

    @Override
    public YmlSettings[] getSettings() {
        return new YmlSettings[0];
    }

    @Override
    protected PluginManagedModule getPlugin() {
        return null;
    }
}
