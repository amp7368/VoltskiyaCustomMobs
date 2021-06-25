package apple.voltskiya.custom_mobs.leaps.pounce;

import apple.nms.decoding.attribute.DecodeAttributeModifier;
import apple.nms.decoding.attribute.DecodeGenericAttributes;
import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.pathfinder.DecodeMoveType;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.leaps.PathfinderGoalLeap;
import apple.voltskiya.custom_mobs.leaps.config.LeapDo;
import apple.voltskiya.custom_mobs.leaps.config.LeapPostConfig;
import apple.voltskiya.custom_mobs.leaps.config.LeapPreConfig;
import apple.voltskiya.custom_mobs.leaps.sounds.LeapSounds;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class LeapPounce {
    private static final int CHARGE_UP_TIME = 30;
    public static final AttributeModifier NO_MOVE_ATTRIBUTE = new AttributeModifier(UUID.randomUUID(), "no_move", -100, DecodeAttributeModifier.Operation.ADDITION.encode());
    public static final int POUNCE_STUN_TIME = 60;

    public static void eatEntity(EntityLiving creature, LeapPreConfig config) {
        if (creature instanceof EntityInsentient) {
            @Nullable EntityLiving lastTarget = ((EntityInsentient) creature).getGoalTarget();

            LeapPostConfig postConfig = new LeapPostConfig(
                    (leapDo) -> shouldStopLeap(creature, leapDo),
                    creature::isOnGround,
                    (entity, leapDo) -> preLeap(entity, leapDo, CHARGE_UP_TIME, 0),
                    (entity) -> interruptedLeap(entity, lastTarget),
                    (entity) -> endLeap(entity, lastTarget)
            );
            final PathfinderGoalLeap pounce = new PathfinderGoalLeap((EntityInsentient) creature, config, postConfig);
            pounce.setMoveType(EnumSet.of(DecodeMoveType.TARGET.encode(), DecodeMoveType.JUMP.encode()));
            DecodeEntity.getGoalSelector((EntityInsentient) creature).a(0, pounce);
        }
    }

    private static boolean shouldStopLeap(EntityLiving creature, @Nullable LeapDo leapDo) {
        if (leapDo != null && leapDo.isMidJump()) {
            List<Entity> nearby = creature.getBukkitEntity().getNearbyEntities(1.5, 2, 1.5);
            for (Entity entity : nearby) {
                if (entity instanceof Player) {
                    pounceStun((Player) entity);
                }
            }
        }
        return DecodeEntity.getHurtTimestamp(creature) >= DecodeEntity.getTicksLived(creature) - 10;
    }

    private static void pounceStun(Player bukkitEntity) {
        bukkitEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, POUNCE_STUN_TIME, 7));
    }

    private static void preLeap(EntityInsentient entity, LeapDo leapDo, int timeLeft, int index) {
        final AttributeModifiable speedAttribute = entity.getAttributeInstance(DecodeGenericAttributes.MOVEMENT_SPEED);
        if (timeLeft == 0) {
            if (speedAttribute != null)
                speedAttribute.removeModifier(NO_MOVE_ATTRIBUTE);
            if (!shouldStopLeap(entity, leapDo)) {
                leapDo.recalculate();
                leapDo.leap();
            } else {
                leapDo.setLeaping(false);
            }
            return;
        }
        if (shouldStopLeap(entity, leapDo)) {
            if (speedAttribute != null)
                speedAttribute.removeModifier(NO_MOVE_ATTRIBUTE);

            leapDo.setLeaping(false);
            return;
        }
        // as long as it doesn't already have the attribute
        if (speedAttribute != null && !speedAttribute.a(NO_MOVE_ATTRIBUTE))
            speedAttribute.addModifier(NO_MOVE_ATTRIBUTE);
        if (index % 3 == 0) {
            LeapSounds.CHARGE_UP_GROWL.accept(entity.getBukkitEntity().getLocation());
        }
        int timeTillNextCheck = Math.min(5, timeLeft);
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> preLeap(entity, leapDo, timeLeft - timeTillNextCheck, index + 1), timeTillNextCheck);
    }

    private static void endLeap(EntityInsentient entity, EntityLiving lastTarget) {
        entity.getBukkitEntity().setVelocity(new Vector(0, 0, 0));
        entity.setGoalTarget(lastTarget);
    }

    private static void interruptedLeap(EntityInsentient entity, EntityLiving lastTarget) {
        entity.getBukkitEntity().setVelocity(new Vector(0, 0, 0));
        entity.setGoalTarget(lastTarget);
    }
}
