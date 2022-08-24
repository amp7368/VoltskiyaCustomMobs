package apple.voltskiya.custom_mobs.mobs.abilities;

import apple.configs.factory.AppleConfigLike;
import apple.lib.pmc.PluginModule;
import apple.mc.utilities.PluginModuleMcUtil;
import apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.bowlike.BowlikeMoveManager;
import apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.fire_fangs.FireFangsConfig;
import apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.flamethrower.FlameThrowerConfig;
import apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.micro_misles.MicroMissileConfig;
import apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.micro_misles.MicroMissileManager;
import apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.shoot_ball.ShootBallConfig;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.fireball.FireballThrowManager;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.lost_soul.BlemishSoulConfig;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.orbital_strike.large.OrbitalStrikeConfig;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.orbital_strike.mancubus.MancubusManager;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.ReviverManager;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.warper.WarperConfig;
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
        return List.of(configYaml(BlemishSoulConfig.class, "BlemishSoulConfig.yml"),
            configYaml(FireFangsConfig.class, "FireFangsConfig.yml"),
            configYaml(FlameThrowerConfig.class, "FlamethrowerConfig.yml"),
            configYaml(MicroMissileConfig.class, "MicroMissileConfig.yml"),
            configYaml(ShootBallConfig.class, "ShootBallConfig.yml"),
            configYaml(OrbitalStrikeConfig.class, "OrbitalStrikeConfig.yml"),
            configYaml(WarperConfig.class, "WarperConfig.yml"));

    }
}
