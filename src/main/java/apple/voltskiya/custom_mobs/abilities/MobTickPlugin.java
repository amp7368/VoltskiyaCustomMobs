package apple.voltskiya.custom_mobs.abilities;

import apple.lib.configs.factory.AppleConfigLike;
import apple.lib.pmc.AppleModule;
import apple.voltskiya.custom_mobs.abilities.ai_changes.bowlike.BowlikeMoveManager;
import apple.voltskiya.custom_mobs.abilities.ai_changes.fire_fangs.FireFangsConfig;
import apple.voltskiya.custom_mobs.abilities.ai_changes.fire_fangs.FireFangsManager;
import apple.voltskiya.custom_mobs.abilities.ai_changes.micro_misles.MicroMissileConfig;
import apple.voltskiya.custom_mobs.abilities.ai_changes.micro_misles.MicroMissileManager;
import apple.voltskiya.custom_mobs.abilities.ai_changes.micro_misles.MicroMissleShooter;
import apple.voltskiya.custom_mobs.abilities.ai_changes.revenant.RevenantSpawner;
import apple.voltskiya.custom_mobs.abilities.ai_changes.shoot_ball.ShootBallConfig;
import apple.voltskiya.custom_mobs.abilities.tick.fireball.FireballThrowManager;
import apple.voltskiya.custom_mobs.abilities.tick.lost_soul.BlemishSoulConfig;
import apple.voltskiya.custom_mobs.abilities.tick.lost_soul.BlemishSpawnManager;
import apple.voltskiya.custom_mobs.abilities.tick.lost_soul.LostSoulManagerTicker;
import apple.voltskiya.custom_mobs.abilities.tick.orbital_strike.large.OrbitalStrikeConfig;
import apple.voltskiya.custom_mobs.abilities.tick.orbital_strike.mancubus.MancubusManager;
import apple.voltskiya.custom_mobs.abilities.tick.reviver.ReviverManager;
import apple.voltskiya.custom_mobs.abilities.tick.warper.WarperConfig;
import java.util.List;


public class MobTickPlugin extends AppleModule {

    private static MobTickPlugin instance;


    @Override
    public void init() {
        instance = this;
    }


    @Override
    public void enable() {
        new RevenantSpawner();
        new MobTickDeathListener();
        new ReviverManager();
        new FireFangsManager();
        new MicroMissileManager();
        new MicroMissleShooter();
        new BlemishSpawnManager();
        new LostSoulManagerTicker();
        new BowlikeMoveManager();
        new FireballThrowManager();
        new MancubusManager();
    }

    public static MobTickPlugin get() {
        return instance;
    }

    @Override
    public String getName() {
        return "MobTick";
    }

    @Override
    public List<AppleConfigLike> getConfigs() {
        return List.of(configJson(BlemishSoulConfig.class, "BlemishSoulConfig"),
            configJson(FireFangsConfig.class, "FireFangsConfig"),
            configJson(MicroMissileConfig.class, "MicroMissileConfig"),
            configJson(ShootBallConfig.class, "ShootBallConfig"),
            configJson(OrbitalStrikeConfig.class, "OrbitalStrikeConfig"),
            configJson(WarperConfig.class, "WarperConfig"));

    }
}
