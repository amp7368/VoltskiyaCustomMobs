package apple.voltskiya.custom_mobs.leaps.upwards;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.leaps.LeapEater;
import apple.voltskiya.custom_mobs.leaps.LeapPlugin;
import apple.voltskiya.custom_mobs.leaps.config.LeapDo;
import apple.voltskiya.custom_mobs.leaps.config.LeapPostConfig;
import apple.voltskiya.custom_mobs.leaps.sounds.LeapSounds;
import apple.voltskiya.custom_mobs.nms.parent.config.ConfigManager;
import apple.voltskiya.custom_mobs.nms.parent.config.YmlSettings;
import net.minecraft.world.entity.Mob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import apple.mc.utilities.AbstractModule;


public class LeapUpwards extends ConfigManager implements LeapEater {


    public void eatEntity(Mob creature) {
        LeapPostConfig postConfig = new LeapPostConfig(
                (leapDo) -> DecodeEntity.getHurtTimestamp(creature) >= DecodeEntity.getTicksLived(creature) - 10,
                () -> DecodeEntity.isOnGround(creature),
                LeapUpwards::preLeap,
                LeapUpwards::interruptedLeap,
                LeapUpwards::endLeap
        );
        DecodeEntity.getGoalSelector(creature).a(0, new GoalLeapUpwards(creature, getConfig(), postConfig));
    }

    private static void preLeap(Mob entity, LeapDo leapDo) {
        Location location = entity.getBukkitEntity().getLocation();
        LeapSounds.CHARGE_UP_GROWL.accept(location);
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), leapDo::leap, 10);
    }

    private static void interruptedLeap(Mob entity) {
        entity.getBukkitEntity().setVelocity(new Vector(0, 0, 0));
    }

    private static void endLeap(Mob entity) {
        entity.getBukkitEntity().setVelocity(new Vector(0, 0, 0));
    }

    /**
     * @return the name of the sub_module (a step below a module)
     */
    @Override
    public String getName() {
        return "upwards_leap";
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
