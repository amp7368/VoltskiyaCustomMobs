package apple.voltskiya.custom_mobs.abilities;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.abilities.ai_changes.micro_misles.MicroMissileManager;
import apple.voltskiya.custom_mobs.abilities.listeners.MobDeathListener;
import apple.voltskiya.custom_mobs.abilities.listeners.MobSpawnListener;
import apple.voltskiya.custom_mobs.abilities.tick.lost_soul.BlemishDeathListener;
import apple.voltskiya.custom_mobs.sql.VerifyMobsSql;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;


public class MobTickPlugin extends VoltskiyaModule {
    private static MobTickPlugin instance;


    @Override
    public void init() {
        instance = this;
    }

    @Override
    public void enable() {
        VerifyMobsSql.initialize();
        new UpdatedPlayerList();
        new MobDeathListener();
        new MobSpawnListener();
        new MicroMissileManager();
        new BlemishDeathListener();
    }

    public static MobTickPlugin get() {
        return instance;
    }

    @Override
    public String getName() {
        return "MobTick";
    }
}
