package apple.voltskiya.custom_mobs.nms;

import apple.voltskiya.custom_mobs.nms.misc.MobHealthPack;
import apple.voltskiya.custom_mobs.nms.nether.revenant.RevenantSpawner;
import com.voltskiya.lib.AbstractModule;

public class NmsPlugin extends AbstractModule {

    @Override
    public void enable() {
        MobHealthPack.spawner().init();
        new RevenantSpawner();
    }

    @Override
    public String getName() {
        return "Nms";
    }
}
