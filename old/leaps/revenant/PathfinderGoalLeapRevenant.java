package apple.voltskiya.custom_mobs.leaps.revenant;

import apple.nms.decoding.block.DecodeBlockPosition;
import apple.nms.decoding.entity.DecodeEntity;
import apple.nms.decoding.pathfinder.DecodeMoveType;
import apple.voltskiya.custom_mobs.leaps.GoalLeap;
import apple.voltskiya.custom_mobs.leaps.config.LeapPostConfig;
import apple.voltskiya.custom_mobs.leaps.config.LeapPreConfig;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.util.RandomPositionGenerator;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftEntity;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.utilities.constants.TagConstants;
import voltskiya.apple.utilities.data_structures.Triple;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class GoalLeapRevenant extends GoalLeap {
    private static final int OUTER_RADIUS = 20;
    private static final int INNER_RADIUS = 5;
    private static final int BELOW_PLAYER_Y = 5;

    /**
     * find a block to navigate to
     *
     * @param me         the entity to navigate
     * @param config     the config for the leap
     * @param postConfig provides any runtime info for the leap
     */
    public GoalLeapRevenant(Mob me, LeapPreConfig config, LeapPostConfig postConfig) {
        super(me, config, postConfig);
        this.setMoveType(EnumSet.of(DecodeMoveType.JUMP.encode(), DecodeMoveType.MOVE.encode()));
    }

    /**
     * @return whether I even want to consider checking whether to run
     */
    @Override
    public boolean a() {
        return this.random.nextInt(config.getCheckInterval()) == 0 &&
                (this.currentLeap == null || !this.currentLeap.isLeaping()) &&
                !this.postConfig.shouldStopCurrentLeap(null) &&
                this.postConfig.isOnGround() &&
                !this.me.getBukkitEntity().getScoreboardTags().contains(TagConstants.IS_DOING_ABILITY);
    }


    /**
     * @return a goal to jump to
     */
    @Nullable
    @Override
    protected Location getGoalLocation() {
        final EntityLiving goalTarget = DecodeEntity.getLastTarget(this.me);
        Location targetLocation;
        if (goalTarget == null) {
            if (this.me instanceof PathfinderMob) {
                BlockPosition loc;
                try {
                    loc = RandomPositionGenerator.a(new Random(), (int) this.config.getDistanceMin(), (int) this.config.getDistanceMax());
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    return null;
                }

                CraftEntity bukkitEntity = this.me.getBukkitEntity();
                Location location = bukkitEntity.getLocation();
                targetLocation = new Location(bukkitEntity.getWorld(),
                        DecodeBlockPosition.getX(loc) + location.getX(),
                        DecodeBlockPosition.getY(loc) + location.getY(),
                        DecodeBlockPosition.getZ(loc) + location.getZ());
            } else {
                return null;
            }
        } else {
            targetLocation = goalTarget.getBukkitEntity().getLocation();
        }
        World world = targetLocation.getWorld();
        final int maxHeight = world.getMaxHeight();
        int x = targetLocation.getBlockX();
        int y = targetLocation.getBlockY();
        int z = targetLocation.getBlockZ();
        List<Triple<Integer, Integer, Integer>> locationToPeaks = new ArrayList<>();
        for (int xi = -OUTER_RADIUS; xi <= OUTER_RADIUS; xi++) {
            if (xi == -INNER_RADIUS) xi = INNER_RADIUS;
            for (int zi = -OUTER_RADIUS; zi <= OUTER_RADIUS; zi++) {
                if (zi == -INNER_RADIUS) zi = INNER_RADIUS;
                int goodY = Short.MIN_VALUE;
                final int xf = x + xi;
                final int zf = z + zi;
                for (int yi = -BELOW_PLAYER_Y; yi < maxHeight; yi++) {
                    if (world.getBlockAt(xf, y + yi, zf).getType().isAir()) break;
                    goodY = y + yi;
                }
                if (goodY != Short.MIN_VALUE) locationToPeaks.add(new Triple<>(xf, goodY, zf));
            }
        }
        locationToPeaks.sort((xyz1, xyz2) -> xyz2.getY() - xyz1.getY());
        int size = locationToPeaks.size();
        // the probability for any peak to be chosen is assigned by the function -index + size()
        // therefore the integral is size*size/2
        int totalProbability = size * size / 2;
        int choice = random.nextInt(Math.max(1, totalProbability));
        int index = 0;
        while (index < size) {
            choice -= -index + size;
            if (choice <= 0) {
                // this is the choice of peak
                Triple<Integer, Integer, Integer> xyz = locationToPeaks.get(index);
                targetLocation.set(xyz.getX(), xyz.getY(), xyz.getZ());
                return targetLocation;
            }
            index++;
        }
        return null;
    }
}
