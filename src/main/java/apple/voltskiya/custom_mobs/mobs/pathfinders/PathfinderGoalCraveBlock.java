package apple.voltskiya.custom_mobs.mobs.pathfinders;

import apple.voltskiya.custom_mobs.PluginDisable;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.PathfinderGoal;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Random;

public class PathfinderGoalCraveBlock extends PathfinderGoal {
    private final EntityInsentient me;
    private final Collection<Material> cravingBlock;
    private final int cravingAmount;
    private final int rangeOfSight;
    private final int checkInterval;
    private final Random random = new Random();
    private final double speed;
    private Vector foundCravedBlock;

    /**
     * find a block to navigate to
     *
     * @param me            the entity to navigate
     * @param cravingBlock  the block(s) that are desired
     * @param cravingAmount the number of *blocks* to look at in a check
     * @param rangeOfSight  the range of sight to look around
     * @param checkInterval how often this should check (keep in mind it's a random chance each time)
     * @param speed         how fast you want the mob to navigate to the location once it's found
     */
    public PathfinderGoalCraveBlock(EntityInsentient me, Collection<Material> cravingBlock, int cravingAmount, int rangeOfSight, int checkInterval, double speed) {
        this.me = me;
        this.cravingBlock = cravingBlock;

        // I cube root this because I check a random cube blocks
        // it's less misleading to do it this way
        this.cravingAmount = (int) Math.pow(cravingAmount, 1 / 3d);
        this.rangeOfSight = rangeOfSight;
        this.checkInterval = checkInterval;
        this.speed = speed;
        this.setMoveType(EnumSet.of(Type.MOVE));
        PluginDisable.addMob(me,this);
    }

    /**
     * @return whether I even want to consider checking whether to run
     */
    @Override
    public boolean a() {
        if (this.random.nextInt(this.checkInterval) == 0) {
            // try to find a good location
            this.findCravedBlock();
            // if our search gave nothing, say we failed
            // otherwise say we did a good
            return this.foundCravedBlock != null;
        }
        return false;
    }

    private void findCravedBlock() {
        // current location
        int midX = (int) me.locX();
        int midY = (int) me.locY();
        int midZ = (int) me.locZ();

        int minX = midX - rangeOfSight;
        int minY = midY - rangeOfSight;
        int minZ = midZ - rangeOfSight;

        // subtract the craving amount because we want to make the midXYZ to be actually the center of the sight
        int maxX = midX + rangeOfSight - cravingAmount;
        int maxY = midY + rangeOfSight - cravingAmount;
        int maxZ = midZ + rangeOfSight - cravingAmount;

        // I do Math.abs because it's possible that cravingAmount is greater than the rangeOfSight
        final int rangeX = maxX - minX;
        final int rangeY = maxY - minY;
        final int rangeZ = maxZ - minZ;
        int choiceX = random.nextInt(Math.abs(rangeX));
        int choiceY = random.nextInt(Math.abs(rangeY));
        int choiceZ = random.nextInt(Math.abs(rangeZ));
        if (rangeX < 0) choiceX = -choiceX;
        if (rangeY < 0) choiceY = -choiceY;
        if (rangeZ < 0) choiceZ = -choiceZ;
        choiceX += minX;
        choiceY += minY;
        choiceZ += minZ;
        int maxChoiceX = choiceX + cravingAmount;
        int maxChoiceY = choiceY + cravingAmount;
        int maxChoiceZ = choiceZ + cravingAmount;
        for (; choiceX <= maxChoiceX; choiceX++) {
            for (; choiceY <= maxChoiceY; choiceY++) {
                for (; choiceZ <= maxChoiceZ; choiceZ++) {
                    org.bukkit.block.Block block = me.getWorld().getWorld().getBlockAt(choiceX, choiceY, choiceZ);
                    if (this.cravingBlock.contains(block.getType())) {
                        // cool! we happened to find the right block!
                        this.foundCravedBlock = new Vector(choiceX, choiceY, choiceZ);
                    }
                }
            }
        }
    }

    /**
     * @return round 2 of whether I want to run ?
     */
    @Override
    public boolean b() {
        // navigationAbstract.m() returns true if the entity is *not* navigating anywhere
        return !this.me.getNavigation().m();
    }

    /**
     * todo does something I'm sure
     *
     * @return something
     */
    @Override
    public boolean C_() {
        return true;
    }

    /**
     * run the pathfinding
     */
    @Override
    public void c() {
        // go to the location
        this.me.getNavigation().a(this.foundCravedBlock.getX(), this.foundCravedBlock.getY(), this.foundCravedBlock.getZ(), speed);
    }

    /**
     * on completion of goal, do what?
     */
    @Override
    public void d() {
        // quit going to the location
        this.me.getNavigation().o();
    }

    /**
     * todo does something I'm sure
     */
    @Override
    public void e() {
    }

    public void setMoveType(EnumSet<Type> moveType) {
        super.a(moveType);
    }
}
