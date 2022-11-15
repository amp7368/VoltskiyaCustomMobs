package apple.voltskiya.custom_mobs.leaps.revenant;

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
import apple.mc.utilities.AbstractModule;

public class LeapRevenant extends ConfigManager implements LeapEater {

    private static void endLeap(Mob entity, EntityLiving lastTarget) {
        entity.getBukkitEntity().setVelocity(new Vector(0, 0, 0));
        if (lastTarget != null)
            DecodeEntity.setGoalTarget(entity, lastTarget);
    }

    private static boolean shouldStopLeap(EntityLiving creature) {
        return DecodeEntity.getHurtTimestamp(creature) >= DecodeEntity.getTicksLived(creature) - 10;
    }

    private static void preLeap(Mob entity, LeapDo leapDo) {
        leapDo.leap();
    }

    public void eatEntity(Mob creature) {
        LeapPostConfig postConfig = new LeapPostConfig(
                (leapDo) -> shouldStopLeap(creature),
                () -> DecodeEntity.isOnGround(creature),
                LeapRevenant::preLeap,
                LeapRevenant::interruptedLeap,
                (entity) -> LeapRevenant.endLeap(entity, DecodeEntity.getLastTarget(entity))
        );
        DecodeEntity.getGoalSelector(creature).a(0, new GoalLeapRevenant(creature, getConfig(), postConfig));
    }

    private static void interruptedLeap(Mob entity) {
    }

    /**
     * @return the name of the sub_module (a step below a module)
     */
    @Override
    public String getName() {
        return "revenant_leap";
    }

    /**
     * @return the default values for the config file
     */
    @Override
    public YmlSettings[] getSettings() {
        return new YmlSettings[0];
    }

    /**
     * @return the module associated with this config
     */
    @Override
    protected AbstractModule getPlugin() {
        return LeapPlugin.get();
    }
}
