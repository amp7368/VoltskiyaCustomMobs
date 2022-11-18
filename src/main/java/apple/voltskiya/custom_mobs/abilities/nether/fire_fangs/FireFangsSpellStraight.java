package apple.voltskiya.custom_mobs.abilities.nether.fire_fangs;

import apple.voltskiya.custom_mobs.abilities.nether.fire_fangs.FireFangsSpawner.FireFangsTypeConfig;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class FireFangsSpellStraight extends FireFangsSpell {

    private final int nextSpawnTime;
    private final int ticksToLive;
    private int nextSpawnCountdown;
    private int count = 2;

    public FireFangsSpellStraight(MMSpawned me, FireFangsTypeConfig type) {
        super(me, type);
        final FireFangLine firstFangLine = this.fangLines.get(0);
        if (firstFangLine != null) {
            this.ticksToLive = firstFangLine.getTicksToLive();
            this.nextSpawnCountdown = this.nextSpawnTime = (int) (ticksToLive * 1.5);
        } else
            this.nextSpawnCountdown = this.nextSpawnTime = this.ticksToLive = 0;
    }


    @Override
    public void stateChoice() {
        if (this.nextSpawnCountdown-- == 0 && count > 0) {
            this.nextSpawnCountdown = this.nextSpawnTime;
            this.count--;
            Location mainLocation = this.getLocation();
            @Nullable LivingEntity goalTarget = this.getTarget();
            Vector mainDirection;
            if (goalTarget == null)
                mainDirection = mainLocation.getDirection().normalize().multiply(config.step);
            else
                mainDirection = goalTarget.getLocation().toVector()
                    .subtract(mainLocation.toVector()).normalize();
            this.fangLines.add(new FireFangLine(mainDirection, mainLocation, this.ticksToLive,
                this.config.fireLength));
        }
        super.stateChoice();
    }

    @Override
    public void cleanUp(boolean isDead) {
        this.count = 2;
    }

    protected boolean hasLines() {
        return !this.fangLines.isEmpty() || this.count >= 0;
    }
}
