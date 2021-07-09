package apple.voltskiya.custom_mobs.old_turrets;

import apple.voltskiya.custom_mobs.ticking.TickGiverable;

import java.util.ArrayList;
import java.util.Iterator;

public class OldTurretIndividualTicker {
    private final OldTurretManagerTicker.Closeness closeness;
    private long tickering;
    private final ArrayList<OldTurretMob> turrets = new ArrayList<>();
    private boolean isTicking = false;

    public OldTurretIndividualTicker(OldTurretManagerTicker.Closeness closeness) {
        this.closeness = closeness;
    }

    public synchronized void giveTurret(OldTurretMob turret) {
        this.turrets.add(turret);
        if (!isTicking) {
            isTicking = true;
            final TickGiverable giver = closeness.getGiver();
            this.tickering = giver.add(this::tick);
        }
    }

    private synchronized void tick() {
        boolean trim = false;
        Iterator<OldTurretMob> turretIterator = turrets.iterator();
        while (turretIterator.hasNext()) {
            OldTurretMob turret = turretIterator.next();
            if (turret.isDead()) {
                turrets.remove(turret);
                return;
            }
            turret.tick();
            if (OldTurretManagerTicker.get().amIGivingTurret(turret, closeness)) {
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
}
