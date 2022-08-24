package apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.config;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobToTick;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.mob.MobReviverBasic;
import org.bukkit.entity.Entity;


public class ReviverConfigBasic extends ReviverConfig {

    public int reviveRitualTime = 20;

    public double searchRadius = 50;

    public int deadCooldown = 1000 * 5;

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
