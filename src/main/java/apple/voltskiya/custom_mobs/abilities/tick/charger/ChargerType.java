package apple.voltskiya.custom_mobs.abilities.tick.charger;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;

public enum ChargerType {
    NORMAL("charger_basic", ChargerChargeSpell::new),
    QUICK("charger_quick", ChargerChargeQuickSpell::new);

    private int overshootDistance;
    private double overshootSpeed;
    private double tooCloseToCharge;
    private double marginOfError;
    private int maxChargeTime;
    private double chargeChance;
    private int chargeCooldown;
    private int chargeUpTime;
    private int chargeStunTime;
    private int chargeTiredTime;
    private final String tag;
    private final BiFunction<Charger, Entity, ChargerChargeSpell> constructor;

    ChargerType(String tag, BiFunction<Charger, Entity, ChargerChargeSpell> constructor) {
        this.tag = tag;
        this.constructor = constructor;
    }

    public void set(int overshootDistance, double overshootSpeed, double tooCloseToCharge, double marginOfError, int maxChargeTime, double normalChargeChance, int normalChargeCooldown, int chargeUpTime, int chargeStunTime, int chargeTiredTime) {
        this.overshootDistance = overshootDistance;
        this.overshootSpeed = overshootSpeed;
        this.tooCloseToCharge = tooCloseToCharge;
        this.marginOfError = marginOfError;
        this.maxChargeTime = maxChargeTime;
        this.chargeChance = normalChargeChance;
        this.chargeCooldown = normalChargeCooldown;
        this.chargeUpTime = chargeUpTime;
        this.chargeStunTime = chargeStunTime;
        this.chargeTiredTime = chargeTiredTime;
    }

    public int getOvershootDistance() {
        return overshootDistance;
    }

    public double getOvershootSpeed() {
        return overshootSpeed;
    }

    public double getTooCloseToCharge() {
        return tooCloseToCharge;
    }

    public double getMarginOfError() {
        return marginOfError;
    }

    public int getMaxChargeTime() {
        return maxChargeTime;
    }

    public double getChargeChance() {
        return chargeChance;
    }

    public int getChargeCooldown() {
        return chargeCooldown;
    }

    public int getChargeUpTime() {
        return chargeUpTime;
    }

    public int getChargeStunTime() {
        return chargeStunTime;
    }

    public int getChargeTiredTime() {
        return chargeTiredTime;
    }

    public String getTag() {
        return tag;
    }

    public ChargerChargeSpell construct(Charger charger, Player location) {
        return this.constructor.apply(charger,location);
    }
}
