package apple.voltskiya.custom_mobs.abilities.common.reviver;

import apple.voltskiya.custom_mobs.abilities.common.reviver.config.ReviverConfigBasic;
import apple.voltskiya.custom_mobs.abilities.common.reviver.config.ReviverConfigPulse;
import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.listen.SpawnListenerHolder;
import java.util.Collection;
import java.util.List;

public class ReviverAbilitySpawner implements SpawnListenerHolder {

    public ReviverConfigBasic basic = new ReviverConfigBasic();

    public ReviverConfigPulse pulse = new ReviverConfigPulse();

    @Override
    public Collection<SpawnListener> getListeners() {
        return List.of(basic, pulse);
    }
}
