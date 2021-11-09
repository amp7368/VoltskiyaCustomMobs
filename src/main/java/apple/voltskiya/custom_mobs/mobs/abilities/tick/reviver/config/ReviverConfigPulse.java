package apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.config;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobToTick;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.mob.MobReviverPulse;
import org.bukkit.entity.Entity;
import ycm.yml.manager.fields.YcmField;

public class ReviverConfigPulse extends ReviverConfig {
    @YcmField
    public int spellCooldown = 20 * 20;
    @YcmField
    public int ticksToChargeUp = 2 * 20;
    @YcmField
    public int ticksForAnger = 2 * 20;
    @YcmField
    public int ticksForPulse = 3 * 20;
    @YcmField
    public int pulseRadius = 10;
    @YcmField
    public int deadTooLong = 1000 * 60 * 10;


    @Override
    public String getTag() {
        return "reviver_pulse";
    }

    @Override
    public MobToTick<ReviverConfigPulse> createMob(Entity entity) {
        return new MobReviverPulse(entity, this);
    }
}
