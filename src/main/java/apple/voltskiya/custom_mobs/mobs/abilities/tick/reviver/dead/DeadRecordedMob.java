package apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.dead;

import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class DeadRecordedMob {
    private static final int DEAD_TOO_LONG = 600000;
    private final NBTTagCompound nbt;
    private final Location location;
    private final EntityType entityType;
    private final long diedAtTime;
    private long cooldownIsUpAt = System.currentTimeMillis() + 5000;

    public DeadRecordedMob(LivingEntity entity) {
        final net.minecraft.world.entity.Entity original = ((CraftEntity) entity).getHandle();
        NBTTagCompound nbt = new NBTTagCompound();
        original.save(nbt);
        this.nbt = nbt;
        this.location = entity.getLocation();
        this.entityType = entity.getType();
        this.diedAtTime = System.currentTimeMillis();
    }

    public NBTTagCompound getNbt() {
        return nbt;
    }

    public Location getLocation() {
        return location;
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
