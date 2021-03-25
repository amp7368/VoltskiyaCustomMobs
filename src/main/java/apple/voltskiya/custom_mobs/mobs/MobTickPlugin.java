package apple.voltskiya.custom_mobs.mobs;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.mobs.listeners.MobDeathListener;
import apple.voltskiya.custom_mobs.mobs.listeners.MobSpawnListener;
import apple.voltskiya.custom_mobs.sql.MobsSql;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;


public class MobTickPlugin extends VoltskiyaModule {


    private static MobTickPlugin instance;

    @Override
    public void enable() {
        instance = this;
        MobsSql.initialize();
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
