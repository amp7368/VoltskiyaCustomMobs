package apple.voltskiya.custom_mobs.mobs.abilities;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.micro_misles.MicroMissileManager;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.lost_soul.BlemishDeathListener;


public class MobTickPlugin extends VoltskiyaModule {
    private static MobTickPlugin instance;


    @Override
    public void init() {
        instance = this;
    }

    @Override
    public void enable() {
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
