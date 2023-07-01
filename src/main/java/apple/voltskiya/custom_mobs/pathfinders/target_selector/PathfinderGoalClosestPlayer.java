package apple.voltskiya.custom_mobs.pathfinders.target_selector;

import apple.nms.decoding.pathfinder.DecodeMoveType;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftMob;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import voltskiya.apple.utilities.minecraft.player.PlayerUtils;

public class PathfinderGoalClosestPlayer extends TargetGoal {

    private final Mob me;
    private final double sight;
    private final boolean seeThroughBlocks;
    private final Random random = new Random();
    private final int checkInterval = 10;
    private boolean isRunning = false;

    public PathfinderGoalClosestPlayer(Mob me, double sight, boolean seeThroughBlocks) {
        super(me, false, false);
        this.me = me;
        this.sight = sight;
        this.seeThroughBlocks = seeThroughBlocks;
        this.setFlags(EnumSet.of(DecodeMoveType.TARGET.encode()));
    }

    @Override
    public boolean canUse() {
        return !this.isRunning && this.me.getTarget() == null &&
            this.random.nextInt(this.checkInterval) == 0;
    }

    @Override
    public void tick() {
        CraftMob bukkitEntity = (CraftMob) this.me.getBukkitEntity();
        Location myLocation = bukkitEntity.getLocation();
        Comparator<Player> comparator = Comparator.comparing(Entity::getLocation, Comparator.comparingDouble(myLocation::distance));
        Optional<Player> closest = myLocation.getNearbyPlayers(
                sight, sight / 2, sight, PlayerUtils::isSurvival).stream()
            .filter((player -> this.seeThroughBlocks || bukkitEntity.hasLineOfSight(player)))
            .min(comparator);
        if (closest.isPresent()) {
            this.me.setTarget(((CraftPlayer) closest.get()).getHandle(), TargetReason.CLOSEST_PLAYER, true);
            isRunning = false;
        }
    }


    @Override
    public boolean canContinueToUse() {
        return this.isRunning;
    }


    @Override
    public void start() {
        this.isRunning = true;
    }

    /**
     * on completion of goal, do what?
     */
    @Override
    public void stop() {
        // quit going to the location
        this.isRunning = false;
    }

}
