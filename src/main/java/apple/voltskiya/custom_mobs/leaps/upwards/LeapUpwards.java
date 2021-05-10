package apple.voltskiya.custom_mobs.leaps.upwards;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.leaps.LeapEater;
import apple.voltskiya.custom_mobs.leaps.LeapPlugin;
import apple.voltskiya.custom_mobs.leaps.config.LeapDo;
import apple.voltskiya.custom_mobs.leaps.config.LeapPostConfig;
import apple.voltskiya.custom_mobs.leaps.sounds.LeapSounds;
import apple.voltskiya.custom_mobs.mobs.ConfigManager;
import apple.voltskiya.custom_mobs.mobs.YmlSettings;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;


public class LeapUpwards extends ConfigManager implements LeapEater {


    public void eatEntity(EntityInsentient creature) {
        LeapPostConfig postConfig = new LeapPostConfig(
                (leapDo) -> creature.hurtTimestamp >= creature.ticksLived - 10,
                creature::isOnGround,
                LeapUpwards::preLeap,
                LeapUpwards::interruptedLeap,
                LeapUpwards::endLeap
        );
        creature.goalSelector.a(0, new PathfinderGoalLeapUpwards(creature, getConfig(), postConfig));
    }

    private static void preLeap(EntityInsentient entity, LeapDo leapDo) {
        Location location = entity.getBukkitEntity().getLocation();
        LeapSounds.CHARGE_UP_GROWL.accept(location);
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), leapDo::leap, 10);
    }

    private static void interruptedLeap(EntityInsentient entity) {
        entity.getBukkitEntity().setVelocity(new Vector(0, 0, 0));
    }

    private static void endLeap(EntityInsentient entity) {
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
    protected VoltskiyaModule getPlugin() {
        return LeapPlugin.get();
    }
}
