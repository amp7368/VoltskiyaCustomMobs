package apple.voltskiya.custom_mobs.abilities.tick.orbital_strike;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.abilities.tick.orbital_strike.OrbitalStrikeConfig.OrbitalStrikeConfigSmall;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.ability.MMAbility;
import java.util.Comparator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.Nullable;


public class MobOrbitalStriker<Config extends OrbitalStrikeConfig> extends MMAbility<Config> {

    public MobOrbitalStriker(MMSpawned mob, Config config) {
        super(mob, config, config.activation());
    }


    @Override
    public void cleanUp(boolean isDead) {
        if (isDead)
            return;
        getMob().setAI(true);
    }

    @Override
    protected boolean canStartAbility() {
        Entity target = this.getTarget();
        if (target == null)
            target = findTarget();
        return target != null;
    }

    @Override
    protected void startAbility() {
        Mob striker = this.getMob();
        LivingEntity target = this.getTarget();
        if (target == null) {
            this.finishAbility();
            return;
        }
        // we have the target. time to orbital strike it
        final Location targetLocation = target.getLocation();
        striker.setAI(false);
        if (config instanceof OrbitalStrikeConfigSmall configSmall)
            OrbitalStrikeSmall.create(this.mob, targetLocation, configSmall);
        else
            new OrbitalStrike<>(targetLocation, config);

        Bukkit.getScheduler()
            .scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this::disable, config.targetTime);
        Bukkit.getScheduler()
            .scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this::finishAbility, config.totalTime);
    }


    @Nullable
    private LivingEntity findTarget() {
        Mob striker = this.getMob();
        List<Entity> nearbyEntities = striker.getNearbyEntities(config.range, config.range,
                config.range).stream().filter(this::testTarget)
            .sorted(Comparator.comparingDouble(this::distanceToTarget)).toList();
        for (Entity nearby : nearbyEntities) {
            if (striker.hasLineOfSight(nearby) && nearby instanceof LivingEntity living) {
                striker.setTarget(living);
                return living;
            }
        }
        return null;
    }

    private double distanceToTarget(Entity entity) {
        return this.getLocation().distance(entity.getLocation());
    }

    private boolean testTarget(Entity entity) {
        return entity.getType() == EntityType.PLAYER
            && entity.getLocation().distance(getLocation()) <= config.range;
    }
}
