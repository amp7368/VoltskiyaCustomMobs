package apple.voltskiya.custom_mobs.abilities.overseer.laser;

import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.listen.SpawnListenerHolder;
import java.util.Collection;
import java.util.List;

public class MissileLaserAbilitySpawner implements SpawnListenerHolder {

    public MissileLaserConfig normal = new MissileLaserConfig();
    public MissileLaserConfig overseer = new MissileLaserConfig();

    @Override
    public Collection<SpawnListener> getListeners() {
        normal.tag = "missile_laser.normal";
        overseer.tag = "missile_laser.overseer";
        return List.of(normal, overseer);
    }

}
