package apple.voltskiya.custom_mobs.sound;

import apple.voltskiya.custom_mobs.sound.leap.LeapSounds;
import com.voltskiya.lib.AbstractModule;
import com.voltskiya.lib.configs.factory.AppleConfigLike;
import java.util.List;

public class SoundModule extends AbstractModule {

    @Override
    public void enable() {
    }

    @Override
    public String getName() {
        return "Sound";
    }

    @Override
    public List<AppleConfigLike> getConfigs() {
        return List.of(configJson(LeapSounds.class, "Leap.sounds", "Leap"));
    }
}
