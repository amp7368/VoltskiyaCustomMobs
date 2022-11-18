package apple.voltskiya.custom_mobs.abilities.tick.orbital_strike;

import apple.voltskiya.custom_mobs.abilities.tick.orbital_strike.OrbitalStrikeConfig.OrbitalStrikeConfigLarge;
import apple.voltskiya.custom_mobs.abilities.tick.orbital_strike.OrbitalStrikeConfig.OrbitalStrikeConfigMedium;
import apple.voltskiya.custom_mobs.abilities.tick.orbital_strike.OrbitalStrikeConfig.OrbitalStrikeConfigSmall;
import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.listen.SpawnListenerHolder;
import java.util.Collection;
import java.util.List;

public class OrbitalStrikeAbilitySpawner implements SpawnListenerHolder {

    public OrbitalStrikeConfigLarge large = new OrbitalStrikeConfigLarge();

    public OrbitalStrikeConfigMedium medium = new OrbitalStrikeConfigMedium();

    public OrbitalStrikeConfigSmall small = new OrbitalStrikeConfigSmall();


    @Override
    public Collection<SpawnListener> getListeners() {
        return List.of(large, medium, small);
    }
}
