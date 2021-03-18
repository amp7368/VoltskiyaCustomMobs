package apple.voltskiya.custom_mobs.heartbeat.tick.revive;

import apple.voltskiya.custom_mobs.heartbeat.tick.MobListSql;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

public class ReviverIndividualTicker {
    private final ReviverManagerTicker.Closeness closeness;
    private boolean isReviving;
    private final ArrayList<UUID> revivers = new ArrayList<>();
    private boolean isTicking = false;
    private final Random random = new Random();
    private long ticker;

    public ReviverIndividualTicker(ReviverManagerTicker.Closeness closeness) {
        this.closeness = closeness;
    }

    public void giveReviver(Entity reviver) {
        this.revivers.add(reviver.getUniqueId());
        if (!isTicking) {
            isTicking = true;
            this.ticker = closeness.getGiver().add(this::tick);
        }
    }

    private void tick() {
        boolean trim = false;
        Iterator<UUID> reviverIterator = revivers.iterator();
        while (reviverIterator.hasNext()) {
            UUID reviverUuid = reviverIterator.next();
            @Nullable Entity reviver = Bukkit.getEntity(reviverUuid);
            if (reviver == null || reviver.isDead()) {
                MobListSql.removeMob(reviverUuid);
                reviverIterator.remove();
                trim = true;
                continue;
            }
            tickReviver(reviver);
            if (ReviverManagerTicker.get().amIGivingReviver(reviver, closeness)) {
                reviverIterator.remove();
                trim = true;
            }
        }
        if (trim) {
            if (revivers.isEmpty()) {
                isTicking = false;
                closeness.getGiver().remove(ticker);
            }
            revivers.trimToSize();
        }
    }

    private void tickReviver(Entity reviver) {
        if (isReviving) {
            if (random.nextDouble() < ReviverManagerTicker.get().REVIVE_CHANCE * closeness.getGiver().getTickSpeed()) {
                revive(reviver);
            }
        }
    }

    private void revive(Entity reviver) {
        ReviveDeadManager.get().revive(reviver.getLocation());
    }

    public void setIsReviving() {
        this.isReviving = true;
    }
}
