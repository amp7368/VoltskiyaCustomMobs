package apple.voltskiya.custom_mobs.mobs.abilities;

import apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.bowlike.BowlikeMoveManager;
import apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.micro_misles.MicroMissileManager;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.fireball.FireballThrowManager;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.lost_soul.BlemishDeathListener;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.mancubus.MancubusManager;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.ReviverManager;
import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;
import voltskiya.apple.configs.plugin.manage.ConfigBuilderHolder;
import voltskiya.apple.configs.plugin.manage.PluginManagedModuleConfig;
import voltskiya.apple.utilities.util.action.PluginManagedRepeatingAction;

import java.util.Collection;
import java.util.Collections;


public class MobTickPlugin extends PluginManagedModule
        implements PluginManagedModuleConfig, PluginManagedRepeatingAction {
    private static MobTickPlugin instance;


    @Override
    public void init() {
        instance = this;
    }

    @Override
    public void enable() {
        new MobTickDeathListener();
        new OldMobTickSpawnListener();
        new MicroMissileManager();
        new BlemishDeathListener();

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
    public Collection<ConfigBuilderHolder<?>> getConfigsToRegister() {
        return Collections.emptyList();
    }
}
