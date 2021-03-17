package apple.voltskiya.custom_mobs.heartbeat.tick.lost_soul;

import apple.voltskiya.custom_mobs.heartbeat.tick.Tickable;
import apple.voltskiya.custom_mobs.heartbeat.tick.main.TickGiverable;
import apple.voltskiya.custom_mobs.heartbeat.tick.main.UpdatedPlayerList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

public class LostSoulIndividualTicker implements Tickable {
    private final TickGiverable giver;
    private final ArrayList<UUID> vexes = new ArrayList<>();
    private final LostSoulManagerTicker.Closeness closeness;
    private boolean isTicking = false;
    private boolean isCheckCollision = false;
    private long myTickerUid = -1;
    private final Random random = new Random();

    public LostSoulIndividualTicker(TickGiverable giver, LostSoulManagerTicker.Closeness closeness) {
        this.giver = giver;
        this.closeness = closeness;
    }

    @Override
    public synchronized void tick() {
        Iterator<UUID> vexUidIterator = vexes.iterator();
        boolean trim = false;
        while (vexUidIterator.hasNext()) {
            UUID vexUid = vexUidIterator.next();
            Entity entity = Bukkit.getEntity(vexUid);
            if (entity == null || entity.getType() != EntityType.VEX) {
                // remove this vex D:
                vexUidIterator.remove();
                trim = true;
            } else {
                Vex vex = (Vex) entity;
                tickVex(vex);
                if (LostSoulManagerTicker.get().amIGivingVex(vex, closeness)) {
                    vexUidIterator.remove();
                    trim = true;
                }
            }
        }
        if (trim) {
            vexes.trimToSize();
            if (isTicking && vexes.isEmpty()) {
                isTicking = false;
                giver.remove(myTickerUid);
            }
        }
    }

    private synchronized void tickVex(Vex vex) {
        if (isCheckCollision) {
            Player player = UpdatedPlayerList.getCollision(vex.getBoundingBox());
            if (player != null) {
                Location location = vex.getLocation();
                location.getWorld().spawnEntity(player.getEyeLocation(), EntityType.WITHER_SKULL, CreatureSpawnEvent.SpawnReason.CUSTOM, e -> {
                    final WitherSkull skull = (WitherSkull) e;
                    skull.setCharged(true);
                    skull.setIsIncendiary(true);

                    final Vector down = new Vector(0, -1, 0);
                    skull.setDirection(down);
                    e.setVelocity(down);

                });
                vex.remove();
            }
        }
    }

    public synchronized void giveVex(Vex vex) {
        this.vexes.add(vex.getUniqueId());
        if (!isTicking) {
            isTicking = true;
            this.myTickerUid = closeness.getGiver().add(this::tick);
        }
    }

    synchronized void setIsCheckCollision() {
        this.isCheckCollision = true;
    }

}
