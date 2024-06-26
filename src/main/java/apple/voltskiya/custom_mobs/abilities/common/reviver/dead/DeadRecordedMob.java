package apple.voltskiya.custom_mobs.abilities.common.reviver.dead;

import apple.nms.decoding.entity.DecodeEntity;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class DeadRecordedMob {

    private static final int DEAD_TOO_LONG = 600000;
    private final CompoundTag nbt;
    private final EntityType entityType;
    private final long diedAtTime;
    private Location location;
    private long cooldownIsUpAt = System.currentTimeMillis() + 1000;

    public DeadRecordedMob(Entity entity) {
        final net.minecraft.world.entity.Entity original = ((CraftEntity) entity).getHandle();
        this.nbt = DecodeEntity.save(original);
        this.location = entity.getLocation();
        this.entityType = entity.getType();
        this.diedAtTime = System.currentTimeMillis();
    }

    public CompoundTag getNbt() {
        return nbt;
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

    public EntityType getEntityType() {
        return entityType;
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
}
