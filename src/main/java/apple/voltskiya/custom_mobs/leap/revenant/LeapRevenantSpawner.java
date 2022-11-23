package apple.voltskiya.custom_mobs.leap.revenant;

import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.listen.SpawnListenerHolder;
import java.util.Collection;
import java.util.List;

public class LeapRevenantSpawner implements SpawnListenerHolder {

    public LeapRevenantConfig basic = new LeapRevenantConfig("revenant.leap.basic");

    @Override
    public Collection<SpawnListener> getListeners() {
        return List.of(this.basic);
    }
}
