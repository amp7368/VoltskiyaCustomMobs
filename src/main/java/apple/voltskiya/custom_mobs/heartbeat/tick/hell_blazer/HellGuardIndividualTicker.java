package apple.voltskiya.custom_mobs.heartbeat.tick.hell_blazer;

import apple.voltskiya.custom_mobs.heartbeat.tick.MobListSql;
import apple.voltskiya.custom_mobs.heartbeat.tick.Tickable;
import apple.voltskiya.custom_mobs.heartbeat.tick.lost_soul.LostSoulManagerTicker;
import apple.voltskiya.custom_mobs.heartbeat.tick.main.TickGiverable;
import apple.voltskiya.custom_mobs.heartbeat.tick.main.UpdatedPlayerList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;

import java.util.Iterator;
import java.util.UUID;

public class HellGuardIndividualTicker implements Tickable {
    public HellGuardIndividualTicker(TickGiverable giver, HellGuardManagerTicker.Closeness closeness) {
//        this.giver = giver;
//        this.closeness = closeness;
    }

    @Override
    public synchronized void tick() {
//        Iterator<UUID> vexUidIterator = vexes.iterator();
//        boolean trim = false;
//        while (vexUidIterator.hasNext()) {
//            UUID vexUid = vexUidIterator.next();
//            Entity entity = Bukkit.getEntity(vexUid);
//            if (entity == null || entity.getType() != EntityType.VEX) {
//        remove this vex D:
//                vexUidIterator.remove();
//                trim = true;
//            } else {
//                Vex vex = (Vex) entity;
//                tickVex(vex);
//                if (LostSoulManagerTicker.get().amIGivingVex(vex, closeness)) {
//                    vexUidIterator.remove();
//                    trim = true;
//                }
//            }
//        }
//        if (trim) {
//            vexes.trimToSize();
//            if (isTicking && vexes.isEmpty()) {
//                giver.remove(myTickerUid);
//                isTicking = false;
//            }
//        }
    }

    private synchronized void tickVex(Vex vex) {
    }

    public synchronized void giveVex(Vex vex) {
//        this.vexes.add(vex.getUniqueId());
//        if (!isTicking) {
//            isTicking = true;
//            this.myTickerUid = closeness.getGiver().add(this::tick);
//        }
    }

    synchronized void setIsCheckCollision() {
//        this.isCheckCollision = true;
    }

    public void giveHellBlazer(LivingEntity hellBlazer) {

    }
}