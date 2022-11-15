package apple.voltskiya.custom_mobs.abilities.nether.fireball;

import apple.voltskiya.custom_mobs.abilities.nether.fireball.config.FireballThrowConfig;
import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.listen.SpawnListenerHolder;
import java.util.Collection;
import java.util.List;

public class FireballAbilitySpawner implements SpawnListenerHolder {

    public final FireballThrowConfig basic1 = new FireballThrowConfig("fireball.basic");
    public final FireballThrowConfig basic2 = new FireballThrowConfig("fireball.basic2");
    public final FireballThrowConfig basic3 = new FireballThrowConfig("fireball.basic3");
    public final FireballThrowConfig basic4 = new FireballThrowConfig("fireball.basic4");
    public final FireballThrowConfig rapid = new FireballThrowConfig("fireball.rapid");

    @Override
    public Collection<SpawnListener> getListeners() {
        return List.of(basic1, basic2, basic3, basic4, rapid);
    }
}
