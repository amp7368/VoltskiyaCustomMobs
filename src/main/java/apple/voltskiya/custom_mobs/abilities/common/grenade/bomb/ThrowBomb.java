package apple.voltskiya.custom_mobs.abilities.common.grenade.bomb;

import apple.voltskiya.custom_mobs.abilities.common.grenade.GrenadeEntityImpact;
import apple.voltskiya.custom_mobs.abilities.common.grenade.ThrowGrenade;
import java.util.List;
import org.bukkit.Location;

public class ThrowBomb extends ThrowGrenade<ThrowBombConfig> {

    public ThrowBomb(ThrowBombConfig throwBombConfig) {
        super(throwBombConfig);
    }

    @Override
    protected void explode(List<GrenadeEntityImpact> impacts) {
        Location location = getLocation();
        location.getWorld().createExplosion(location, config.explosionPower, false, false);
    }

    @Override
    protected double explosionRadius() {
        return config.explosionRadius;
    }
}
