package apple.voltskiya.custom_mobs.abilities;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.abilities.listeners.MobDeathListener;
import apple.voltskiya.custom_mobs.abilities.listeners.MobSpawnListener;
import apple.voltskiya.custom_mobs.sql.VerifyMobsSql;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;


public class MobTickPlugin extends VoltskiyaModule {


    private static MobTickPlugin instance;

    @Override
    public void enable() {
        instance = this;
        VerifyMobsSql.initialize();
        new UpdatedPlayerList();
        new MobDeathListener();
        new MobSpawnListener();
    }

    public static MobTickPlugin get() {
        return instance;
    }

    @Override
    public String getName() {
        return "MobTick";
    }
}
