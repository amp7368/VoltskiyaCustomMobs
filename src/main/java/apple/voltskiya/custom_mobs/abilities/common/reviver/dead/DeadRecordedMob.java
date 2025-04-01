package apple.voltskiya.custom_mobs.abilities.common.reviver.dead;

import apple.mc.utilities.data.serialize.EntitySerializable;
import java.util.function.Consumer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class DeadRecordedMob {

    private static final int DEAD_TOO_LONG = 600000;
    private final long diedAtTime;
    private final EntitySerializable entity;
    private Location location;
    private long cooldownIsUpAt = System.currentTimeMillis() + 1000;

    public DeadRecordedMob(Entity entity) {
        this.entity = new EntitySerializable(entity);
        this.location = entity.getLocation();
        this.diedAtTime = System.currentTimeMillis();
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void resetCooldown(int ticksTillOkay) {
        this.cooldownIsUpAt = System.currentTimeMillis() + ticksTillOkay * 50L;
    }

    public boolean isCooldownUp() {
        return System.currentTimeMillis() >= cooldownIsUpAt;
    }

    public void remove() {
        ReviveDeadManager.removeMob(this);
    }

    public boolean isDeadTooLong() {
        return isDeadTooLong(DEAD_TOO_LONG);
    }

    public boolean isDeadTooLong(int deadTooLong) {
        return diedAtTime + deadTooLong < System.currentTimeMillis();
    }

    public void spawn(@Nullable Consumer<Entity> handleSpawn) {
        entity.spawn(location, handleSpawn);
    }
}
