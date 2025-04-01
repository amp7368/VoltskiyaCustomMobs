package apple.voltskiya.custom_mobs.abilities.nether.lost_soul;

import apple.voltskiya.custom_mobs.abilities.tick.Tickable;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;
import apple.voltskiya.custom_mobs.util.ticking.TickGiverable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vex;

public class LostSoulIndividualTicker implements Tickable {

    private final TickGiverable giver;
    private final ArrayList<UUID> vexes = new ArrayList<>();
    private final LostSoulManagerTicker.Closeness closeness;
    private boolean isTicking = false;
    private boolean isCheckCollision = false;
    private long myTickerUid = -1;


    public LostSoulIndividualTicker(TickGiverable giver,
        LostSoulManagerTicker.Closeness closeness) {
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
                giver.remove(myTickerUid);
                isTicking = false;
            }
        }
    }

    private synchronized void tickVex(Vex vex) {
        if (!isCheckCollision)
            return;
        Player player = UpdatedPlayerList.getCollision(vex.getBoundingBox());
        if (player != null) {
            Location location = vex.getLocation();
            location.getWorld().spawnParticle(Particle.EXPLOSION, player.getEyeLocation(), 0);
            location.getWorld()
                .playSound(player.getEyeLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
            player.damage(LostSoulManagerTicker.get().DAMAGE_AMOUNT, vex);
            vex.remove();
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
