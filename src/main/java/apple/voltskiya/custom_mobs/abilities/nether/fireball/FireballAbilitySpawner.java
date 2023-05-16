package apple.voltskiya.custom_mobs.abilities.nether.fireball;

import apple.voltskiya.custom_mobs.abilities.nether.fireball.config.FireballThrowConfig;
import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.listen.SpawnListenerHolder;
import com.google.gson.Gson;
import java.util.Collection;
import java.util.List;

public class FireballAbilitySpawner implements SpawnListenerHolder {

    public final FireballThrowConfig basic = new FireballThrowConfig();
    public final FireballThrowConfig rapid = new FireballThrowConfig();

    @Override
    public Collection<SpawnListener> getListeners() {
        basic.tag = "fireball.basic";
        rapid.tag = "fireball.rapid";
        return List.of(basic, rapid);
    }
}
