package apple.voltskiya.custom_mobs.heartbeat.tick.revive;

import apple.voltskiya.custom_mobs.heartbeat.tick.DeathEater;
import net.kyori.adventure.text.EntityNBTComponent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.EntityDeathEvent;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ReviveDeadManager extends DeathEater {

    public long MAX_DEAD_TIME;
    private static ReviveDeadManager instance;
    private final HashMap<Entity, Long> dead = new HashMap<>();

    public ReviveDeadManager() throws IOException {
        instance = this;
        MAX_DEAD_TIME = (int) getValueOrInit(getName(), YmlSettings.MAX_DEAD_TIME.getPath(), "dead") * 1000 / 20;
    }


    public static ReviveDeadManager get() {
        return instance;
    }

    @Override
    public void eatEvent(EntityDeathEvent event) {
        final long now = System.currentTimeMillis();
        dead.entrySet().removeIf(uuid -> now - uuid.getValue() > MAX_DEAD_TIME);
        this.dead.put(event.getEntity(), now);
        // don't add it to the db
    }

    @Override
    public String getName() {
        return "revive";
    }

    @Override
    public void initializeYml() throws IOException {
        for (YmlSettings setting : YmlSettings.values()) {
            setValueIfNotExists(getName(), setting.getPath(), setting.value,"dead");
        }
    }

    public void revive(Location location) {
        Iterator<Map.Entry<Entity, Long>> iterator = dead.entrySet().iterator();
        System.out.println("revivenow");

        while (iterator.hasNext()) {
            Map.Entry<Entity, Long> uuid = iterator.next();
            Entity mob = uuid.getKey();
            if (mob == null) {
                iterator.remove();
                continue;
            }

            if (mob.getLocation().distance(location) <= MAX_DEAD_TIME) {
                revive(mob);
                iterator.remove();
            }
        }
    }

    private void revive(Entity mob) {
        System.out.println("mobRevived");
    }

    private enum YmlSettings {
        MAX_DEAD_TIME("max_dead_time", 400);

        private final String path;
        private final Object value;

        YmlSettings(String path, Object value) {
            this.path = path;
            this.value = value;
        }

        public String getPath() {
            return path;
        }

        public Object getValue() {
            return value;
        }
    }
}
