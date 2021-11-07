package apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.config;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.parent.MobToTick;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.mob.MobReviverPulse;
import org.bukkit.entity.Entity;

public class ReviverConfigPulse extends ReviverConfig {
    @Override
    public String getTag() {
        return "reviver_pulse";
    }

    @Override
    public MobToTick<ReviverConfigPulse> createMob(Entity entity) {
        return new MobReviverPulse(entity, this);
    }
}
