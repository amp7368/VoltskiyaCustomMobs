package apple.voltskiya.custom_mobs.leaps.reposition;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.leaps.LeapEater;
import apple.voltskiya.custom_mobs.leaps.LeapPlugin;
import apple.voltskiya.custom_mobs.leaps.config.LeapDo;
import apple.voltskiya.custom_mobs.leaps.config.LeapPostConfig;
import apple.voltskiya.custom_mobs.nms.parent.config.ConfigManager;
import apple.voltskiya.custom_mobs.nms.parent.config.YmlSettings;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityLiving;
import org.bukkit.util.Vector;
import apple.mc.utilities.AppleModule;

public class LeapReposition extends ConfigManager implements LeapEater {
    private static boolean shouldStopLeap(EntityLiving creature) {
        return DecodeEntity.getHurtTimestamp(creature) >= DecodeEntity.getTicksLived(creature) - 10;
    }

    private static void preLeap(Mob entity, LeapDo leapDo) {
        leapDo.leap();
    }

    private static void endLeap(Mob entity, EntityLiving lastTarget) {
        entity.getBukkitEntity().setVelocity(new Vector(0, 0, 0));
        if (lastTarget != null)
            DecodeEntity.setGoalTarget(entity, lastTarget);
    }

    private static void interruptedLeap(Mob entity) {
    }

    public void eatEntity(Mob creature) {
        LeapPostConfig postConfig = new LeapPostConfig(
                (leapDo) -> shouldStopLeap(creature),
                () -> DecodeEntity.isOnGround(creature),
                LeapReposition::preLeap,
                LeapReposition::interruptedLeap,
                (entity) -> LeapReposition.endLeap(entity, DecodeEntity.getLastTarget(entity))
        );
        DecodeEntity.getGoalSelector(creature).a(0, new GoalLeapReposition(creature, getConfig(), postConfig));
    }

    @Override
    public String getName() {
        return "leap_reposition";
    }

    @Override
    public YmlSettings[] getSettings() {
        return new YmlSettings[0];
    }

    @Override
    protected AppleModule getPlugin() {
        return LeapPlugin.get();
    }
}
