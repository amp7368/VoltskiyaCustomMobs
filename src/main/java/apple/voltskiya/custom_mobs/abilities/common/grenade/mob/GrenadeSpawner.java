package apple.voltskiya.custom_mobs.abilities.common.grenade.mob;

import apple.voltskiya.custom_mobs.abilities.common.grenade.flashbang.FlashbangConfig;
import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.listen.SpawnListenerHolder;
import java.util.Collection;
import java.util.List;

public class GrenadeSpawner implements SpawnListenerHolder {

    protected FlashbangConfig flashbang = new FlashbangConfig();

    @Override
    public Collection<SpawnListener> getListeners() {
        return List.of(this.flashbang);
    }
}
