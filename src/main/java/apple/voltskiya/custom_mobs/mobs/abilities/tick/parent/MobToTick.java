package apple.voltskiya.custom_mobs.mobs.abilities.tick.parent;

import net.minecraft.world.entity.EntityInsentient;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import voltskiya.apple.utilities.util.constants.TagConstants;

public abstract class MobToTick<Config extends MobConfig> {
    protected Entity bukkitEntity;
    protected net.minecraft.world.entity.Entity nmsEntity;
    protected Config config;
    protected boolean isDead;

    public MobToTick(Entity bukkitEntity, Config config) {
        this.bukkitEntity = bukkitEntity;
        this.nmsEntity = ((CraftEntity) bukkitEntity).getHandle();
        this.config = config;
    }

    public abstract void tick(int tickSpeed);

    public Entity getBukkitEntity() {
        return bukkitEntity;
    }

    public net.minecraft.world.entity.Entity getNmsEntity() {
        return nmsEntity;
    }

    public EntityInsentient getEntityInsentient() {
        return (EntityInsentient) nmsEntity;
    }

    public Mob getBukkitMob() {
        return (Mob) bukkitEntity;
    }

    public Location getLocation() {
        return bukkitEntity.getLocation();
    }

    public World getWorld() {
        return getLocation().getWorld();
    }

    public void removeTicking() {
        this.isDead = true;
    }

    public boolean isNotDoingAction() {
        return !isDoingAction();
    }

    public boolean isDoingAction() {
        return bukkitEntity.getScoreboardTags().contains(TagConstants.isDoingAbility);
    }

    public boolean shouldRemove() {
        return isDead || bukkitEntity.isDead();
    }

    public void tick_(int tickSpeed) {
        if (isNotDoingAction()) {
            tick(tickSpeed);
        }
    }
}
