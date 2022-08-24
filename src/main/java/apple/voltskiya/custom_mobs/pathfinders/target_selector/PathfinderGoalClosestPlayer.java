package apple.voltskiya.custom_mobs.pathfinders.target_selector;

import apple.mc.utilities.world.vector.VectorUtils;
import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.pathfinder.DecodeMoveType;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftMob;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PathfinderGoalClosestPlayer extends Goal {

    private final Mob me;
    private final double sight;
    private final boolean seeThroughBlocks;
    private final Random random = new Random();
    private final int checkInterval = 10;
    private Player newTarget = null;
    private boolean isRunning = false;

    public PathfinderGoalClosestPlayer(Mob me, double sight, boolean seeThroughBlocks) {
        this.me = me;
        this.sight = sight;
        this.seeThroughBlocks = seeThroughBlocks;
        this.setFlags(EnumSet.of(DecodeMoveType.TARGET.encode()));
    }

    @Override
    public boolean canUse() {
        if (this.isRunning || DecodeEntity.getLastTarget(this.me) != null
            || this.random.nextInt(this.checkInterval) != 0) {
            return false;
        }

        CraftMob bukkitEntity = (CraftMob) this.me.getBukkitEntity();
        Location myLocation = bukkitEntity.getLocation();
        @NotNull Collection<org.bukkit.entity.Player> nearbyPlayers = myLocation.getNearbyPlayers(
            sight, sight / 2, sight, p -> p.getGameMode() == GameMode.SURVIVAL);
        for (Iterator<org.bukkit.entity.Player> iterator = nearbyPlayers.iterator();
            iterator.hasNext(); ) {
            org.bukkit.entity.Player nearby = iterator.next();
            Vector difference = nearby.getLocation().subtract(myLocation).toVector();
            myLocation.setDirection(difference);
            @Nullable RayTraceResult rayTrace = myLocation.getWorld()
                .rayTrace(myLocation, myLocation.getDirection(), sight, FluidCollisionMode.NEVER,
                    true, 0.01, null);
            if (rayTrace == null || rayTrace.getHitEntity() != nearby) {
                iterator.remove();
                continue;
            }
            Block hitBlock = rayTrace.getHitBlock();
            if (hitBlock != null) {
                double distanceToBlock = VectorUtils.distance(myLocation, hitBlock.getLocation());
                double distanceToEntity = VectorUtils.magnitude(difference);
                if (distanceToEntity > distanceToBlock) {
                    iterator.remove();
                }
            }
        }
        Optional<org.bukkit.entity.Player> closest = nearbyPlayers.stream().min(
            Comparator.comparingDouble(p -> VectorUtils.distance(p.getLocation(), myLocation)));
        if (closest.isPresent()) {
            newTarget = ((CraftPlayer) closest.get()).getHandle();
            isRunning = true;
            return true;
        }
        return false;
    }


    @Override
    public boolean canContinueToUse() {
        return this.isRunning;
    }

    @Override
    public void start() {
        if (newTarget != null) {
            this.me.setTarget(newTarget, EntityTargetEvent.TargetReason.CLOSEST_PLAYER, true);
        }
        this.isRunning = false;
        this.newTarget = null;
    }

    /**
     * on completion of goal, do what?
     */
    @Override
    public void stop() {
        // quit going to the location
    }
}
