package apple.voltskiya.custom_mobs.turret.infinite;

import apple.voltskiya.custom_mobs.turret.parent.TurretTargeting;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class TurretTargettingInfinite extends TurretTargeting {
    @Override
    public boolean shouldTargetPlayer(Player player) {
        return true;
    }

    @Override
    public boolean isTargetHostile(LivingEntity entity) {
        return true;
    }

    @Override
    public boolean isTargetEntity(LivingEntity entity) {
        return false;
    }
}
