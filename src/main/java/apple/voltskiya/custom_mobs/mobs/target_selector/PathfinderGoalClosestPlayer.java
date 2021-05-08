package apple.voltskiya.custom_mobs.mobs.target_selector;

import apple.voltskiya.custom_mobs.PluginDisable;
import net.minecraft.server.v1_16_R3.EntityHuman;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.PathfinderGoal;
import net.minecraft.server.v1_16_R3.PathfinderTargetCondition;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Random;

public class PathfinderGoalClosestPlayer extends PathfinderGoal {
    private final EntityInsentient me;
    private final double sight;
    private final boolean seeThroughBlocks;
    private Random random = new Random();
    private int checkInterval = 10;
    private EntityHuman newTarget = null;
    private boolean isRunning = false;

    public PathfinderGoalClosestPlayer(EntityInsentient me, double sight, boolean seeThroughBlocks) {
        this.me = me;
        this.sight = sight;
        this.seeThroughBlocks = seeThroughBlocks;
        this.setMoveType(EnumSet.of(Type.TARGET));
        PluginDisable.addMob(me, this);
    }

    @Override
    public boolean a() {
        if (!this.isRunning && this.me.getGoalTarget() == null && this.random.nextInt(this.checkInterval) == 0) {
            @Nullable EntityHuman player = this.me.world.b(EntityHuman.class,
                    new PathfinderTargetCondition().a(this.sight).a((e) -> e.getBukkitEntity() instanceof Player && ((Player) e.getBukkitEntity()).getGameMode() == GameMode.SURVIVAL),
                    this.me,
                    this.me.locX(),
                    this.me.locY(),
                    this.me.locZ(),
                    this.me.getBoundingBox().grow(sight, sight / 2, sight)
            );
            if ((this.newTarget = player) == null) {
                return false;
            } else {
                this.isRunning = true;
                return true;
            }
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
            this.me.setGoalTarget(newTarget, EntityTargetEvent.TargetReason.CLOSEST_PLAYER, true);
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
