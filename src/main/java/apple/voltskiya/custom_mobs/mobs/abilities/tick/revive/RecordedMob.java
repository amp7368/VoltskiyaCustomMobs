package apple.voltskiya.custom_mobs.mobs.abilities.tick.revive;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import static apple.voltskiya.custom_mobs.mobs.abilities.tick.revive.ReviveDeadManager.MAX_DEAD_TIME;

public class RecordedMob {

    private final double health;
    public final Location location;
    private final LivingEntity entity;
    private long diedTime = System.currentTimeMillis();
    private boolean isReviving;
    private boolean shouldRemove = false;

    public RecordedMob(LivingEntity entity) {
        @Nullable AttributeInstance hp = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        this.health = hp == null ? 0 : hp.getBaseValue();
        this.location = entity.getLocation();
        this.entity = entity;
    }

    public double getHealth() {
        return health;
    }

    public boolean isNearby(Location loc, Reviver reviverObject) {
        return location.getWorld().equals(loc.getWorld()) && location.distance(loc) <= reviverObject.getReviveDistance();
    }

    public boolean isReviveableNow() {
        return System.currentTimeMillis() - diedTime > ReviveDeadManager.DIED_COOLDOWN && !isReviving;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public void resetCooldown() {
        this.isReviving = false;
        diedTime = System.currentTimeMillis();
    }

    public boolean shouldRemove() {
        final long now = System.currentTimeMillis();
        return now - diedTime > MAX_DEAD_TIME || shouldRemove;
    }

    public void setReviving() {
        isReviving = true;
    }

    public void remove() {
        this.shouldRemove = true;
    }
}
