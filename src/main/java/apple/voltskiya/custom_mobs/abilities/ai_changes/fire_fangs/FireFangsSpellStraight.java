package apple.voltskiya.custom_mobs.abilities.ai_changes.fire_fangs;

import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityLiving;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class FireFangsSpellStraight extends FireFangsSpell {
    private final int nextSpawnTime;
    private final int ticksToLive;
    private int nextSpawnCountdown;
    private int count = 2;

    public FireFangsSpellStraight(EntityInsentient me, FireFangs.FangsType type) {
        super(me, type);
        final FireFangLine firstFangLine = this.fangLines.get(0);
        if (firstFangLine != null) {
            this.ticksToLive = firstFangLine.getTicksToLive();
            this.nextSpawnCountdown = this.nextSpawnTime = (int) (ticksToLive * 1.5);
        } else
            this.nextSpawnCountdown = this.nextSpawnTime = this.ticksToLive = 0;
    }


    @Override
    public void run() {
        if (this.nextSpawnCountdown-- == 0 && count > 0) {
            this.nextSpawnCountdown = this.nextSpawnTime;
            this.count--;
            Location mainLocation = this.me.getBukkitEntity().getLocation();
            final EntityLiving goalTarget = this.me.getGoalTarget();
            Vector mainDirection;
            if (goalTarget == null) mainDirection = mainLocation.getDirection().normalize().multiply(type.getStep());
            else
                mainDirection = goalTarget.getBukkitEntity().getLocation().toVector().subtract(mainLocation.toVector()).normalize();
            this.fangLines.add(new FireFangLine(
                    mainDirection,
                    mainLocation,
                    this.ticksToLive,
                    this.type.getFireLength()
            ));
        }
        super.run();
    }

    @Override
    protected boolean shouldRun() {
        return super.shouldRun() || this.count >= 0;
    }
}
