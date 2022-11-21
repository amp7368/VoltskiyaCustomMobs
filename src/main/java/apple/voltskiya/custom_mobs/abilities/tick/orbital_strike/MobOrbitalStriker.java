package apple.voltskiya.custom_mobs.abilities.tick.orbital_strike;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.abilities.tick.orbital_strike.OrbitalStrikeConfig.OrbitalStrikeConfigSmall;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.ability.MMAbility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;


public class MobOrbitalStriker<Config extends OrbitalStrikeConfig> extends MMAbility<Config> {

    public MobOrbitalStriker(MMSpawned mob, Config config) {
        super(mob, config);
    }


    @Override
    public void cleanUp(boolean isDead) {
        if (isDead)
            return;
        getMob().setAI(true);
    }

    @Override
    protected boolean canStartAbility() {
        return this.hasTarget();
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

        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this::disable, config.targetTime);
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this::finishAbility, config.totalTime);
    }
}
