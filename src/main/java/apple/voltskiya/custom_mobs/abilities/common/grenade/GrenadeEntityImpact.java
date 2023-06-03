package apple.voltskiya.custom_mobs.abilities.common.grenade;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public record GrenadeEntityImpact(Entity entity, double hitPercentage, double distance) {

    public double hitImpactLog(double maxDistance) {
        double impact = 1 - this.distance / maxDistance;
        return this.hitPercentage * impact * impact;
    }

    public boolean isPlayer() {
        return entity instanceof Player;
    }

    public Player getPlayer() {
        return (Player) entity;
    }
}
