package apple.voltskiya.custom_mobs.abilities;

import apple.configs.factory.AppleConfigLike;
import apple.lib.pmc.PluginModule;
import apple.mc.utilities.PluginModuleMcUtil;
import apple.voltskiya.custom_mobs.abilities.ai_changes.bowlike.BowlikeMoveManager;
import apple.voltskiya.custom_mobs.abilities.tick.lost_soul.BlemishSoulConfig;
import apple.voltskiya.custom_mobs.abilities.tick.warper.WarperConfig;
import apple.voltskiya.custom_mobs.abilities.ai_changes.fire_fangs.FireFangsConfig;
import apple.voltskiya.custom_mobs.abilities.ai_changes.flamethrower.FlameThrowerConfig;
import apple.voltskiya.custom_mobs.abilities.ai_changes.micro_misles.MicroMissileConfig;
import apple.voltskiya.custom_mobs.abilities.ai_changes.micro_misles.MicroMissileManager;
import apple.voltskiya.custom_mobs.abilities.ai_changes.shoot_ball.ShootBallConfig;
import apple.voltskiya.custom_mobs.abilities.tick.fireball.FireballThrowManager;
import apple.voltskiya.custom_mobs.abilities.tick.orbital_strike.large.OrbitalStrikeConfig;
import apple.voltskiya.custom_mobs.abilities.tick.orbital_strike.mancubus.MancubusManager;
import apple.voltskiya.custom_mobs.abilities.tick.reviver.ReviverManager;
import java.util.List;


public class MobTickPlugin extends PluginModule implements PluginModuleMcUtil {

    private static MobTickPlugin instance;


    @Override
    public void init() {
        instance = this;
    }

    @Override
    public void enable() {
        new MobTickDeathListener();
        new MicroMissileManager();

        new BowlikeMoveManager();
        new ReviverManager();
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
        return List.of(configYaml(BlemishSoulConfig.class, "BlemishSoulConfig"),
            configYaml(FireFangsConfig.class, "FireFangsConfig"),
            configYaml(FlameThrowerConfig.class, "FlamethrowerConfig"),
            configYaml(MicroMissileConfig.class, "MicroMissileConfig"),
            configYaml(ShootBallConfig.class, "ShootBallConfig"),
            configYaml(OrbitalStrikeConfig.class, "OrbitalStrikeConfig"),
            configYaml(WarperConfig.class, "WarperConfig"));

    }
}
