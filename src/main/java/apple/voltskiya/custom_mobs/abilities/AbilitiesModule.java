package apple.voltskiya.custom_mobs.abilities;

import apple.voltskiya.custom_mobs.abilities.common.micro_missile.MicroMissileConfig;
import apple.voltskiya.custom_mobs.abilities.common.micro_missile.MicroMissileManager;
import apple.voltskiya.custom_mobs.abilities.common.micro_missile.MicroMissileShooter;
import apple.voltskiya.custom_mobs.abilities.common.reviver.ReviverAbilitySpawner;
import apple.voltskiya.custom_mobs.abilities.common.reviver.dead.ReviveDeadManager;
import apple.voltskiya.custom_mobs.abilities.nether.charger.ChargerConfig;
import apple.voltskiya.custom_mobs.abilities.nether.charger.ChargerManagerTicker;
import apple.voltskiya.custom_mobs.abilities.nether.fire_fangs.FireFangsSpawner;
import apple.voltskiya.custom_mobs.abilities.nether.fireball.FireballAbilitySpawner;
import apple.voltskiya.custom_mobs.abilities.nether.lost_soul.BlemishSoulConfig;
import apple.voltskiya.custom_mobs.abilities.nether.lost_soul.BlemishSpawnManager;
import apple.voltskiya.custom_mobs.abilities.nether.lost_soul.LostSoulManagerTicker;
import apple.voltskiya.custom_mobs.abilities.nether.mancubus.MancubusAbilitySpawner;
import apple.voltskiya.custom_mobs.abilities.nether.warper.WarperConfigSpawner;
import apple.voltskiya.custom_mobs.abilities.overseer.laser.MissileLaserAbilitySpawner;
import apple.voltskiya.custom_mobs.abilities.tick.orbital_strike.OrbitalStrikeAbilitySpawner;
import apple.voltskiya.mob_manager.listen.SpawnListenerHolder;
import com.voltskiya.lib.AbstractModule;
import com.voltskiya.lib.configs.data.config.AppleConfig;
import com.voltskiya.lib.configs.data.config.AppleConfig.Builder;
import com.voltskiya.lib.configs.factory.AppleConfigLike;
import java.util.ArrayList;
import java.util.List;


public class AbilitiesModule extends AbstractModule {

    private static AbilitiesModule instance;
    private final List<AppleConfig<? extends SpawnListenerHolder>> configs = new ArrayList<>();


    public AbilitiesModule() {
        instance = this;
    }

    public static AbilitiesModule get() {
        return instance;
    }

    @Override
    public void enable() {
        configs.stream().map(AppleConfig::getInstance).forEach(SpawnListenerHolder::registerListeners);

        new ReviveDeadManager();
        new ChargerManagerTicker();
        new MicroMissileManager();
        new MicroMissileShooter();
        new BlemishSpawnManager();
        new LostSoulManagerTicker();
    }

    @Override
    public String getName() {
        return "Ability";
    }

    @Override
    public List<AppleConfigLike> getConfigs() {
        return List.of(add(configJson(ReviverAbilitySpawner.class, "ReviverConfig", "Reviver")),
            add(configJson(FireballAbilitySpawner.class, "FireballConfig", "Fireball")),
            add(configJson(MancubusAbilitySpawner.class, "MancubusConfig", "Mancubus")),
            add(configJson(MissileLaserAbilitySpawner.class, "MissileLaserConfig", "MissileLaser")),
            add(configJson(OrbitalStrikeAbilitySpawner.class, "OrbitalStrikeConfig", "OrbitalStrike")),
            add(configJson(FireFangsSpawner.class, "FireFangsConfig", "FireFangs")),
            add(configJson(WarperConfigSpawner.class, "WarperConfig")),
            configJson(ChargerConfig.class, "ChargerConfig"),
            configJson(BlemishSoulConfig.class, "BlemishSoulConfig"),
            configJson(MicroMissileConfig.class, "MicroMissileConfig"));

    }

    private AppleConfigLike add(Builder<? extends SpawnListenerHolder> builder) {
        this.configs.add(builder.getConfig());
        return builder;
    }
}
