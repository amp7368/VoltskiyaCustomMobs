package apple.voltskiya.custom_mobs.mobs.abilities.tick.charger;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.charger.ChargerConfig.ChargerTypeConfig;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;

public enum ChargerType {
    NORMAL(ChargerConfig.get().normal,"charger_basic", ChargerChargeSpell::new),
    QUICK(ChargerConfig.get().quick,"charger_quick", ChargerChargeQuickSpell::new);

    private ChargerTypeConfig config;
    private final String tag;
    private final BiFunction<Charger, Entity, ChargerChargeSpell> constructor;

    ChargerType(ChargerTypeConfig config, String tag, BiFunction<Charger, Entity, ChargerChargeSpell> constructor) {
        this.config = config;
        this.tag = tag;
        this.constructor = constructor;
    }

    public int getOvershootDistance() {
        return config.overshootDistance;
    }

    public double getOvershootSpeed() {
        return config.overshootSpeed;
    }

    public double getTooCloseToCharge() {
        return config.tooCloseToCharge;
    }

    public double getMarginOfError() {
        return config.marginOfError;
    }

    public int getMaxChargeTime() {
        return config.maxChargeTime;
    }

    public double getChargeChance() {
        return config.chargeChance;
    }

    public int getChargeCooldown() {
        return config.chargeCooldown;
    }

    public int getChargeUpTime() {
        return config.chargeUpTime;
    }

    public int getChargeStunTime() {
        return config.chargeStunTime;
    }

    public int getChargeTiredTime() {
        return config.chargeTiredTime;
    }

    public String getTag() {
        return tag;
    }

    public ChargerChargeSpell construct(Charger charger, Player location) {
        return this.constructor.apply(charger,location);
    }
}
