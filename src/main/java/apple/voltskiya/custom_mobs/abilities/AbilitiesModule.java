package apple.voltskiya.custom_mobs.abilities;

import apple.voltskiya.custom_mobs.abilities.ai_changes.shoot_ball.ShootBallConfig;
import apple.voltskiya.custom_mobs.abilities.ai_changes.shoot_ball.ShootBallManager;
import apple.voltskiya.custom_mobs.abilities.common.micro_missile.MicroMissileConfig;
import apple.voltskiya.custom_mobs.abilities.common.micro_missile.MicroMissileManager;
import apple.voltskiya.custom_mobs.abilities.common.micro_missile.MicroMissileShooter;
import apple.voltskiya.custom_mobs.abilities.common.reviver.ReviverAbilitySpawner;
import apple.voltskiya.custom_mobs.abilities.common.reviver.dead.ReviveDeadManager;
import apple.voltskiya.custom_mobs.abilities.nether.fire_fangs.FireFangsConfig;
import apple.voltskiya.custom_mobs.abilities.nether.fire_fangs.FireFangsManager;
import apple.voltskiya.custom_mobs.abilities.nether.fireball.FireballAbilitySpawner;
import apple.voltskiya.custom_mobs.abilities.nether.lost_soul.BlemishSoulConfig;
import apple.voltskiya.custom_mobs.abilities.nether.lost_soul.BlemishSpawnManager;
import apple.voltskiya.custom_mobs.abilities.nether.lost_soul.LostSoulManagerTicker;
import apple.voltskiya.custom_mobs.abilities.nether.warper.WarperConfig;
import apple.voltskiya.custom_mobs.abilities.tick.orbital_strike.large.OrbitalStrikeConfig;
import apple.voltskiya.custom_mobs.abilities.tick.orbital_strike.mancubus.MancubusAbilitySpawner;
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


    @Override
    public void enable() {
        new FireFangsManager();
        new ReviveDeadManager();

        configs.stream().map(AppleConfig::getInstance)
            .forEach(SpawnListenerHolder::registerListeners);

        new MicroMissileManager();
        new MicroMissileShooter();
        new ShootBallManager();
        new BlemishSpawnManager();
        new LostSoulManagerTicker();
    }

    public static AbilitiesModule get() {
        return instance;
    }

    @Override
    public String getName() {
        return "MobTick";
    }

    @Override
    public List<AppleConfigLike> getConfigs() {
        return List.of(add(configJson(ReviverAbilitySpawner.class, "ReviverConfig", "Reviver")),
            add(configJson(FireballAbilitySpawner.class, "FireballConfig", "Fireball")),
            add(configJson(MancubusAbilitySpawner.class, "MancubusConfig", "Mancubus")),
            configJson(BlemishSoulConfig.class, "BlemishSoulConfig"),
            configJson(FireFangsConfig.class, "FireFangsConfig"),
            configJson(MicroMissileConfig.class, "MicroMissileConfig"),
            configJson(ShootBallConfig.class, "ShootBallConfig"),
            configJson(OrbitalStrikeConfig.class, "OrbitalStrikeConfig"),
            configJson(WarperConfig.class, "WarperConfig"));

    }

    private AppleConfigLike add(Builder<? extends SpawnListenerHolder> builder) {
        this.configs.add(builder.getConfig());
        return builder;
    }
}
