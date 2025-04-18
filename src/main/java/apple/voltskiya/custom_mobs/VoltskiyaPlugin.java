package apple.voltskiya.custom_mobs;

import apple.voltskiya.custom_mobs.abilities.AbilitiesModule;
import apple.voltskiya.custom_mobs.ai.AiModule;
import apple.voltskiya.custom_mobs.leap.LeapModule;
import apple.voltskiya.custom_mobs.nms.NmsPlugin;
import apple.voltskiya.custom_mobs.sound.SoundModule;
import apple.voltskiya.custom_mobs.turret.TurretModule;
import apple.voltskiya.custom_mobs.util.PluginUtils;
import apple.voltskiya.custom_mobs.util.ticking.Ticking;
import com.voltskiya.lib.AbstractModule;
import com.voltskiya.lib.AbstractVoltPlugin;
import java.util.Collection;
import java.util.List;


public class VoltskiyaPlugin extends AbstractVoltPlugin {

    private static VoltskiyaPlugin instance;

    public VoltskiyaPlugin() {
        instance = this;
    }

    public static VoltskiyaPlugin get() {
        return instance;
    }

    @Override
    public Collection<AbstractModule> getModules() {
        return List.of(new Ticking(), new SoundModule(), // this has to go first
            new PluginUtils(), new TurretModule(), new AbilitiesModule(), new NmsPlugin(), new LeapModule(), new AiModule());
    }

}
