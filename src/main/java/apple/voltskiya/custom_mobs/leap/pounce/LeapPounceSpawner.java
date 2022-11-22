package apple.voltskiya.custom_mobs.leap.pounce;

import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.listen.SpawnListenerHolder;
import java.util.Collection;
import java.util.List;

public class LeapPounceSpawner implements SpawnListenerHolder {

    private PounceConfig basic = new PounceConfig("pounce.basic");

    @Override
    public Collection<SpawnListener> getListeners() {
        return List.of(basic);
    }
}
