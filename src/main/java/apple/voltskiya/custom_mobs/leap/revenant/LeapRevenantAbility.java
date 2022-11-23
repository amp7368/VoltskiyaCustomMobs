package apple.voltskiya.custom_mobs.leap.revenant;

import apple.voltskiya.custom_mobs.leap.LeapModule;
import apple.voltskiya.custom_mobs.leap.parent.CreateLeapStage;
import apple.voltskiya.custom_mobs.leap.parent.Leap;
import apple.voltskiya.custom_mobs.leap.parent.LeapAbility;
import apple.voltskiya.custom_mobs.leap.parent.LeapMath;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class LeapRevenantAbility<Config extends LeapRevenantConfig> extends LeapAbility<Config> {

    private static final Comparator<Location> COMPARE_HEIGHT = Comparator.comparingDouble(Location::getY).reversed();

    public LeapRevenantAbility(MMSpawned mob, Config config) {
        super(mob, config);
    }

    @Override
    protected List<CreateLeapStage<Config>> leapStages() {
        return List.of(Leap::new);
    }


    @Override
    protected boolean isAbilityBlocking() {
        return false;
    }

    @Nullable
    @Override
    protected Location findTarget() {
        @Nullable LivingEntity goalTarget = getTarget();
        LeapMath math = this.config.leap.math();
        Location vagueLocation;
        if (goalTarget == null) {
            double randomDistance = math.rangeBounds() * random.nextDouble() + math.minRange();
            vagueLocation = getLocation().add(getLocation().getDirection().multiply(randomDistance));
        } else {
            vagueLocation = goalTarget.getLocation();
        }
        World world = getWorld();
        int y = vagueLocation.getBlockY();
        int yMax = Math.min(world.getMaxHeight(), y + (int) math.maxPeakHeight());
        int yMin = Math.max(world.getMinHeight(), y + this.config.peaks.belowTargetY);
        if (yMin >= yMax)
            return null;
        int x = vagueLocation.getBlockX();
        int z = vagueLocation.getBlockZ();
        List<Location> peaks = new ArrayList<>();
        int radiusToScan = config.peaks.radiusToScan;
        for (int xi = -radiusToScan; xi <= radiusToScan; xi++) {
            for (int zi = -radiusToScan; zi <= radiusToScan; zi++) {
                Location peak = scanColumn(world, yMax, yMin, x + xi, z + zi);
                if (peak != null)
                    peaks.add(peak);
            }
        }
        int tooCloseRadius = config.peaks.preferOutsideRadius;
        peaks.sort(COMPARE_HEIGHT.thenComparingDouble(xyz -> Math.abs(vagueLocation.distance(xyz) - tooCloseRadius)));
        int size = peaks.size();
        if (size == 0)
            return null;
        // the probability for any peak to be chosen is assigned by the function -index + size()
        // therefore the integral is size*size/2
        int totalProbability = size * size / 2;
        int choice = random.nextInt(totalProbability);
        int index = 0;
        while (index < size) {
            choice -= size + size - index;
            if (choice <= 0) {
                // this is the choice of peak
                return peaks.get(index);
            }
            index++;
        }
        LeapModule.get().logger().error("LeapRevenant had an illegal random 'choice':" + choice);
        return peaks.get(0);
    }

    private Location scanColumn(World world, int yMax, int yMin, int x, int z) {
        boolean hasGround = false;
        for (int yi = yMin; yi < yMax; yi++) {
            if (world.getBlockAt(x, yi, z).getType().isAir()) {
                if (hasGround)
                    return new Location(world, x, yi, z);
            } else
                hasGround = true;
        }
        return null;
    }
}
