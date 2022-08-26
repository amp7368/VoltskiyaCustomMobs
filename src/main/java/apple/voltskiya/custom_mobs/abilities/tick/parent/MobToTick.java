package apple.voltskiya.custom_mobs.abilities.tick.parent;

import apple.nms.decoding.entity.DecodeEntity;
import net.minecraft.world.entity.Mob;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.utilities.minecraft.TagConstants;

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

    public Mob getMob() {
        return (Mob) nmsEntity;
    }

    public org.bukkit.entity.Mob getBukkitMob() {
        return (org.bukkit.entity.Mob) bukkitEntity;
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
        return bukkitEntity.getScoreboardTags().contains(TagConstants.IS_DOING_ABILITY);
    }

    public void setIsDoingAction(boolean isDoingAction) {
        if (isDoingAction)
            TagConstants.addIsDoingAbility(bukkitEntity);
        else
            TagConstants.removeIsDoingAbility(bukkitEntity);
    }

    public boolean wasHit(int inLast) {
        Mob mob = getMob();
        int hurt = DecodeEntity.getHurtTimestamp(mob);
        int ticksLived = DecodeEntity.getTicksLived(mob);

        return hurt != 0 && hurt + inLast >= ticksLived && getBukkitMob().getLastDamage() != 0;
    }

    public @Nullable LivingEntity getTarget() {
        return getBukkitMob().getTarget();
    }

    public boolean hasTarget() {
        return getTarget() != null;
    }

    // custom information about the mob
    public boolean shouldRemove() {
        return isDead();
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

    protected boolean isDead() {
        return isDead || bukkitEntity.isDead();
    }

    protected abstract void kill();
}
