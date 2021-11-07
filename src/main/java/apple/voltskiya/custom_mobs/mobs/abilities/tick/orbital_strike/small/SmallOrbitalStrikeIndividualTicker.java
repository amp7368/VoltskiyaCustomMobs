package apple.voltskiya.custom_mobs.mobs.abilities.tick.orbital_strike.small;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.Tickable;
import apple.voltskiya.custom_mobs.sql.MobListSql;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.utilities.util.DistanceUtils;
import voltskiya.apple.utilities.util.constants.TagConstants;
import voltskiya.apple.utilities.util.data_structures.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

public class SmallOrbitalStrikeIndividualTicker implements Tickable {

    private final SmallOrbitalStrikeManagerTicker.Closeness closeness;
    private boolean isCheckStrike = false;
    private final ArrayList<Pair<UUID, Long>> strikers = new ArrayList<>();
    private boolean isTicking = false;
    private long myTickerUid;
    private final Random random = new Random();


    public SmallOrbitalStrikeIndividualTicker(SmallOrbitalStrikeManagerTicker.Closeness closeness) {
        this.closeness = closeness;
    }

    public synchronized void giveStriker(Entity striker, long lastShot) {
        this.strikers.add(new Pair<>(striker.getUniqueId(), lastShot));
        if (!isTicking) {
            isTicking = true;
            this.myTickerUid = closeness.getGiver().add(this::tick);
        }
    }

    @Override
    public synchronized void tick() {
        Iterator<Pair<UUID, Long>> strikerUidIterator = strikers.iterator();
        boolean trim = false;
        long now = System.currentTimeMillis();
        while (strikerUidIterator.hasNext()) {
            Pair<UUID, Long> strikerUid = strikerUidIterator.next();
            if (now - strikerUid.getValue() < SmallOrbitalStrikeManagerTicker.get().STRIKE_COOLDOWN) {
                continue;
            }
            Entity striker = Bukkit.getEntity(strikerUid.getKey());
            if (striker == null) {
                // remove this striker D:
                MobListSql.removeMob(strikerUid.getKey());
                strikerUidIterator.remove();
                trim = true;
            } else {
                tickStriker(striker, strikerUid);
                if (SmallOrbitalStrikeManagerTicker.get().amIGivingStriker(striker, closeness, strikerUid.getValue())) {
                    MobListSql.removeMob(strikerUid.getKey());
                    strikerUidIterator.remove();
                    trim = true;
                }
            }
        }
        if (trim) {
            if (isTicking && strikers.isEmpty()) {
                closeness.getGiver().remove(myTickerUid);
                isTicking = false;
            }
            strikers.trimToSize();
        }
    }

    private synchronized void tickStriker(Entity striker, Pair<UUID, Long> strikerUid) {
        if (isCheckStrike && !striker.getScoreboardTags().contains(TagConstants.isDoingAbility)) {
            if (random.nextDouble() < SmallOrbitalStrikeManagerTicker.get().STRIKE_CHANCE * closeness.getGiver().getTickSpeed()) {
                checkStrike(striker, strikerUid);
            }
        }
    }

    private synchronized void checkStrike(Entity striker, Pair<UUID, Long> strikerUid) {
        Location strikerLocation = striker.getLocation();
        @Nullable LivingEntity target = ((Mob) striker).getTarget();
        if (target == null) {
            Player closest = UpdatedPlayerList.getClosestPlayerPlayer(striker.getLocation());
            if (closest != null) {
                Location pLocation = closest.getLocation();
                double d = DistanceUtils.distance(pLocation, strikerLocation);
                if (d < SmallOrbitalStrikeManagerTicker.get().STRIKE_DISTANCE && ((Mob) striker).hasLineOfSight(closest)) {
                    target = closest;
                }
            }
        }
        if (target != null) {
            // we have the target. time to orbital strike it
            new OrbitalStrikeSmall(striker, target);
            strikerUid.setValue(System.currentTimeMillis());
        }
    }

    public synchronized void setIsCheckStrike() {
        this.isCheckStrike = true;
    }

}

