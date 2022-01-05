package apple.voltskiya.custom_mobs.leaps.reposition;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.leaps.LeapEater;
import apple.voltskiya.custom_mobs.leaps.LeapPlugin;
import apple.voltskiya.custom_mobs.leaps.config.LeapDo;
import apple.voltskiya.custom_mobs.leaps.config.LeapPostConfig;
import apple.voltskiya.custom_mobs.mobs.nms.parent.config.ConfigManager;
import apple.voltskiya.custom_mobs.mobs.nms.parent.config.YmlSettings;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import org.bukkit.util.Vector;
import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;

public class LeapReposition extends ConfigManager implements LeapEater {
    private static boolean shouldStopLeap(EntityLiving creature) {
        return DecodeEntity.getHurtTimestamp(creature) >= DecodeEntity.getTicksLived(creature) - 10;
    }

    private static void preLeap(EntityInsentient entity, LeapDo leapDo) {
        leapDo.leap();
    }

    private static void endLeap(EntityInsentient entity, EntityLiving lastTarget) {
        entity.getBukkitEntity().setVelocity(new Vector(0, 0, 0));
        if (lastTarget != null)
            DecodeEntity.setGoalTarget(entity, lastTarget);
    }

    private static void interruptedLeap(EntityInsentient entity) {
    }

    public void eatEntity(EntityInsentient creature) {
        LeapPostConfig postConfig = new LeapPostConfig(
                (leapDo) -> shouldStopLeap(creature),
                () -> DecodeEntity.isOnGround(creature),
                LeapReposition::preLeap,
                LeapReposition::interruptedLeap,
                (entity) -> LeapReposition.endLeap(entity, DecodeEntity.getLastTarget(entity))
        );
        DecodeEntity.getGoalSelector(creature).a(0, new PathfinderGoalLeapReposition(creature, getConfig(), postConfig));
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
    protected PluginManagedModule getPlugin() {
        return LeapPlugin.get();
    }
}
