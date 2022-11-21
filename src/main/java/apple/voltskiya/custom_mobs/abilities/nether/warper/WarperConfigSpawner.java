package apple.voltskiya.custom_mobs.abilities.nether.warper;

import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.listen.SpawnListenerHolder;
import java.util.Collection;
import java.util.List;

public class WarperConfigSpawner implements SpawnListenerHolder {

    private WarperConfig warper = new WarperConfig("warper");
    private WarperConfig warperBroken = new WarperConfig("warper.broken");

    @Override
    public Collection<SpawnListener> getListeners() {
        return List.of(this.warper, this.warperBroken);
    }
}
