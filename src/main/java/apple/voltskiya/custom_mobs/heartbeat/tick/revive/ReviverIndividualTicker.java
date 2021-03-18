package apple.voltskiya.custom_mobs.heartbeat.tick.revive;

import apple.voltskiya.custom_mobs.heartbeat.tick.MobListSql;
import com.destroystokyo.paper.entity.Pathfinder;
import net.minecraft.server.v1_16_R3.BlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftMob;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
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

    public synchronized void giveReviver(Entity reviver) {
        this.revivers.add(reviver.getUniqueId());
        if (!isTicking) {
            isTicking = true;
            this.ticker = closeness.getGiver().add(this::tick);
        }
    }

    private synchronized void tick() {
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

    private synchronized void tickReviver(Entity reviver) {
        if (isReviving) {
            if (random.nextDouble() < ReviverManagerTicker.get().REVIVE_CHANCE * closeness.getGiver().getTickSpeed()) {
                revive(reviver);
            }
        }
    }

    private synchronized void revive(Entity entity) {
        ReviveDeadManager.RecordedMob mobToRevive = ReviveDeadManager.get().revive(entity.getLocation());
        if (mobToRevive != null && entity instanceof Mob) {
            CraftMob reviver = (CraftMob) entity;
            @Nullable Pathfinder.PathResult path = reviver.getPathfinder().findPath(mobToRevive.getEntity().getLocation());
            if (path != null) {
                Location target = mobToRevive.getEntity().getLocation();
                int x = target.getBlockX();
                int y = target.getBlockY();
                int z = target.getBlockZ();
            }
        }
    }

    public void setIsReviving() {
        this.isReviving = true;
    }
}
