package apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.config;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobToTick;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.mob.MobReviverBasic;
import org.bukkit.entity.Entity;
import ycm.yml.manager.fields.YcmField;

public class ReviverConfigBasic extends ReviverConfig {
    @YcmField
    public int reviveRitualTime = 20;
    @YcmField
    public double searchRadius = 50;
    @YcmField
    public int deadCooldown = 1000 * 5;
    @YcmField
    public int deadTooLong = 1000 * 60 * 10;

    @Override
    public String getTag() {
        return "reviver";
    }

    @Override
    public MobToTick<ReviverConfigBasic> createMob(Entity entity) {
        return new MobReviverBasic(entity, this);
    }
}
