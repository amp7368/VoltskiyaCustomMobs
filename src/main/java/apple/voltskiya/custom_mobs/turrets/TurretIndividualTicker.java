package apple.voltskiya.custom_mobs.turrets;

import apple.voltskiya.custom_mobs.ticking.TickGiverable;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;

import java.util.ArrayList;
import java.util.Iterator;

public class TurretIndividualTicker {
    private final TurretManagerTicker.Closeness closeness;
    private long tickering;
    private boolean isTargeting = false;
    private final long callerUid = UpdatedPlayerList.callerUid();
    private final ArrayList<TurretMob> turrets = new ArrayList<>();
    private boolean isTicking = false;

    public TurretIndividualTicker(TurretManagerTicker.Closeness closeness) {
        this.closeness = closeness;
    }

    public synchronized void giveTurret(TurretMob turret) {
        this.turrets.add(turret);
        if (!isTicking) {
            isTicking = true;
            final TickGiverable giver = closeness.getGiver();
            this.tickering = giver.add(this::tick);
        }
    }

    private synchronized void tick() {
        boolean trim = false;
        Iterator<TurretMob> turretIterator = turrets.iterator();
        while (turretIterator.hasNext()) {
            TurretMob turret = turretIterator.next();
            if (turret.isDead()) {
                turrets.remove(turret);
                return;
            }
            turret.tick();
            if (TurretManagerTicker.get().amIGivingTurret(turret, closeness)) {
                turretIterator.remove();
                trim = true;
            }
        }
        if (trim) {
            if (turrets.isEmpty()) {
                closeness.getGiver().remove(tickering);
                this.isTicking = false;
            }
            turrets.trimToSize();
        }
    }

    public synchronized void setIsTargeting() {
        this.isTargeting = true;
    }
}
