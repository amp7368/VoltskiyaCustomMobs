package apple.voltskiya.custom_mobs.abilities.common.grenade.flashbang;

import apple.voltskiya.custom_mobs.abilities.common.grenade.GrenadeEntityImpact;
import apple.voltskiya.custom_mobs.abilities.common.grenade.ThrowGrenade;
import java.util.List;

public class ThrowFlashbang extends ThrowGrenade<FlashbangConfig> {

    public ThrowFlashbang(FlashbangConfig config) {
        super(config);
    }

    @Override
    protected void explode(List<GrenadeEntityImpact> impacts) {
        for (GrenadeEntityImpact impact : impacts) {
            if (impact.isPlayer())
                flashbangPlayerEffect(impact);
            else
                flashbangMobEffect(impact);
        }

    }

    @Override
    protected double explosionRadius() {
        return 10;
    }

    private void flashbangMobEffect(GrenadeEntityImpact impact) {

    }

    private void flashbangPlayerEffect(GrenadeEntityImpact impact) {
        double hitImpact = impact.hitImpactLog(explosionRadius());
        double blindnessDuration = this.config.maxBlindnessDuration * hitImpact;
    }
}
