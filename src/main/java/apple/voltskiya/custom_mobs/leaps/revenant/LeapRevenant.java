package apple.voltskiya.custom_mobs.leaps.revenant;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.leaps.LeapEater;
import apple.voltskiya.custom_mobs.leaps.LeapPlugin;
import apple.voltskiya.custom_mobs.leaps.config.LeapDo;
import apple.voltskiya.custom_mobs.leaps.config.LeapPostConfig;
import apple.voltskiya.custom_mobs.mobs.ConfigManager;
import apple.voltskiya.custom_mobs.mobs.YmlSettings;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import org.bukkit.util.Vector;

public class LeapRevenant extends ConfigManager implements LeapEater {

    public void eatEntity(EntityInsentient creature) {
        LeapPostConfig postConfig = new LeapPostConfig(
                (leapDo) -> shouldStopLeap(creature),
                creature::isOnGround,
                LeapRevenant::preLeap,
                LeapRevenant::interruptedLeap,
                (entity) -> LeapRevenant.endLeap(entity, entity.getGoalTarget())
        );
        DecodeEntity.getGoalSelector(creature).a(0, new PathfinderGoalLeapRevenant(creature, getConfig(), postConfig));
    }

    private static boolean shouldStopLeap(EntityLiving creature) {
        return DecodeEntity.getHurtTimestamp(creature) >= DecodeEntity.getTicksLived(creature) - 10;
    }

    private static void preLeap(EntityInsentient entity, LeapDo leapDo) {
        leapDo.leap();
    }

    private static void endLeap(EntityInsentient entity, EntityLiving lastTarget) {
        entity.getBukkitEntity().setVelocity(new Vector(0, 0, 0));
        if (lastTarget != null)
            entity.setGoalTarget(lastTarget);
    }

    private static void interruptedLeap(EntityInsentient entity) {
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
    protected VoltskiyaModule getPlugin() {
        return LeapPlugin.get();
    }
}
