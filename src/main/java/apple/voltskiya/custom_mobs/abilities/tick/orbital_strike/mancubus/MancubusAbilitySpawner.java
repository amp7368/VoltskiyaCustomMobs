package apple.voltskiya.custom_mobs.abilities.tick.orbital_strike.mancubus;

import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.listen.SpawnListenerHolder;
import java.util.Collection;
import java.util.List;

public class MancubusAbilitySpawner implements SpawnListenerHolder {

    public MancubusConfig basic = new MancubusConfig();

    @Override
    public Collection<SpawnListener> getListeners() {
        return List.of(basic);
    }
}
