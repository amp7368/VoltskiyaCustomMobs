package apple.voltskiya.custom_mobs.pathfinders.target_selector;

import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.pathfinder.DecodeMoveType;
import apple.voltskiya.custom_mobs.reload.PluginDisable;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import apple.mc.utilities.world.vector.VectorUtils;

import java.util.*;

public class GoalClosestPlayer extends Goal {
    private final Mob me;
    private final double sight;
    private final boolean seeThroughBlocks;
    private final Random random = new Random();
    private final int checkInterval = 10;
    private Player newTarget = null;
    private boolean isRunning = false;

    public GoalClosestPlayer(Mob me, double sight, boolean seeThroughBlocks) {
        this.me = me;
        this.sight = sight;
        this.seeThroughBlocks = seeThroughBlocks;
        this.setMoveType(EnumSet.of(DecodeMoveType.TARGET.encode()));
        PluginDisable.addMob(me, this);
    }

    @Override
    public boolean a() {
        if (this.isRunning || DecodeEntity.getLastTarget(this.me) != null || this.random.nextInt(this.checkInterval) != 0) {
            return false;
        }

        Mob bukkitEntity = (Mob) this.me.getBukkitEntity();
        Location myLocation = bukkitEntity.getLocation();
        @NotNull Collection<Player> nearbyPlayers = myLocation.getNearbyPlayers(sight, sight / 2, sight,
                p -> p.getGameMode() == GameMode.SURVIVAL);
        for (Iterator<Player> iterator = nearbyPlayers.iterator(); iterator.hasNext(); ) {
            Player nearby = iterator.next();
            Vector difference = nearby.getLocation().subtract(myLocation).toVector();
            myLocation.setDirection(difference);
            @Nullable RayTraceResult rayTrace = myLocation.getWorld().rayTrace(myLocation, myLocation.getDirection(), sight, FluidCollisionMode.NEVER, true, 0.01, null);
            if (rayTrace == null || rayTrace.getHitEntity() != nearby) {
                iterator.remove();
                continue;
            }
            Block hitBlock = rayTrace.getHitBlock();
            if (hitBlock != null) {
                double distanceToBlock = VectorUtils.magnitude(myLocation, hitBlock.getLocation());
                double distanceToEntity = VectorUtils.magnitude(difference);
                if (distanceToEntity > distanceToBlock) {
                    iterator.remove();
                }
            }
        }
        Optional<Player> closest = nearbyPlayers.stream().min(Comparator.comparingDouble(p -> VectorUtils.magnitude(p.getLocation(), myLocation)));
        if (closest.isPresent()) {
            newTarget = ((CraftPlayer) closest.get()).getHandle();
            isRunning = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean b() {
        return this.isRunning;
    }

    @Override
    public void c() {
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
    public void d() {
        // quit going to the location
    }

    public void setMoveType(EnumSet<Type> moveType) {
        super.a(moveType);
    }
}
