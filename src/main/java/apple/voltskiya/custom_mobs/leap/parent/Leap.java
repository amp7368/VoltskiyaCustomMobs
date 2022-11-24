package apple.voltskiya.custom_mobs.leap.parent;

import apple.voltskiya.custom_mobs.leap.parent.config.LeapConfig;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import org.bukkit.Location;
import org.bukkit.entity.Mob;
import org.bukkit.util.Vector;

public class Leap<Config extends LeapConfig> extends LeapStage<Config> {

    private Vector velocity = new Vector();
    private Vector gravity = new Vector(0, fullConfig.leap.math().gravity(), 0);

    public Leap(MMSpawned mob, Config fullConfig, Location targetLocation) {
        super(mob, fullConfig, fullConfig.leap_stage, targetLocation);
        LeapCalculation calculation = new LeapCalculation(this.targetLocation.clone(), this.getLocation(), fullConfig.leap);
        if (calculation.isPossible())
            this.velocity = calculation.initialVelocity();
        else
            this.cancel();
    }


    public void tick() {
        Mob mob = getMob();
        mob.setVelocity(this.velocity);
        mob.lookAt(mob.getLocation().add(this.velocity.getX(), 0, this.velocity.getZ()));
        this.velocity.add(gravity);
    }

    protected boolean isFinished() {
        return super.isFinished() || isOnGround();
    }

    private boolean isOnGround() {
        return getMob().isOnGround() && this.tick >= 5;
    }

}
