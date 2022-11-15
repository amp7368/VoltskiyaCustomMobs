package apple.voltskiya.custom_mobs.abilities.nether.charger;

import org.bukkit.entity.Entity;

public class ChargerChargeQuickSpell extends ChargerChargeSpell {
    private final Entity target;
    private int chargeCount = 0;

    public ChargerChargeQuickSpell(Charger charger, Entity target) {
        super(charger, target);
        this.target = target;
    }

    @Override
    public void stateChoice() {
        if (state == ChargingState.HIT_WALL || state == ChargingState.TIRED)
            if (chargeCount++ < 2) {
                state = ChargingState.CHARGE_UP;
                setFinalLocation(this.charger, this.target);
            }
        super.stateChoice();
    }
}
