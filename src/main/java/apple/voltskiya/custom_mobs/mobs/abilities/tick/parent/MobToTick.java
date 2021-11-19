package apple.voltskiya.custom_mobs.mobs.abilities.tick.parent;

import apple.nms.decoding.entity.DecodeEntity;
import net.minecraft.world.entity.EntityInsentient;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.utilities.util.constants.TagConstants;

public abstract class MobToTick<Config extends MobTickerConfig> {
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

    // get entity
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

    // utility about mob
    public Location getLocation() {
        return bukkitEntity.getLocation();
    }

    public Location getEyeLocation() {
        return getBukkitMob().getEyeLocation();
    }

    public int getTicksLived() {
        return bukkitEntity.getTicksLived();
    }

    public World getWorld() {
        return getLocation().getWorld();
    }

    // action
    public boolean isNotDoingAction() {
        return !isDoingAction();
    }

    public boolean isDoingAction() {
        return bukkitEntity.getScoreboardTags().contains(TagConstants.isDoingAbility);
    }

    public void setIsDoingAction(boolean isDoingAction) {
        if (isDoingAction)
            TagConstants.addIsDoingAbility(bukkitEntity);
        else
            TagConstants.removeIsDoingAbility(bukkitEntity);
    }

    public boolean wasHit(int inLast) {
        EntityInsentient mob = getEntityInsentient();
        int hurt = DecodeEntity.getHurtTimestamp(mob);
        int ticksLived = DecodeEntity.getTicksLived(mob);

        return hurt != 0 && hurt + inLast >= ticksLived && getBukkitMob().getLastDamage() != 0;
    }

    @Nullable
    public LivingEntity getTarget() {
        return getBukkitMob().getTarget();
    }

    // custom information about the mob
    public boolean shouldRemove() {
        return isDead || bukkitEntity.isDead();
    }

    public void tick_(int tickSpeed) {
        if (isNotDoingAction()) {
            tick(tickSpeed);
        }
    }

    public void killIfNotDead() {
        if (!this.isDead) {
            this.isDead = true;
            kill();
        }
    }

    protected abstract void kill();
}
