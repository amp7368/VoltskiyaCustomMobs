package apple.voltskiya.custom_mobs.jumps.tick;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.jumps.LeapPlugin;
import apple.voltskiya.custom_mobs.mob_tick.tick.SpawnEater;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.io.IOException;

public class LeapTickerManager extends SpawnEater {

    public LeapTickerManager() throws IOException {
    }

    @Override
    public void eatEvent(CreatureSpawnEvent event) {
        // deal with eating stuff
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void initializeYml() throws IOException {

    }

    @Override
    protected VoltskiyaModule getPlugin() {
        return LeapPlugin.get();
    }
}
