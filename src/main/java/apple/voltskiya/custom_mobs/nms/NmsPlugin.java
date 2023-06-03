package apple.voltskiya.custom_mobs.nms;

import apple.voltskiya.custom_mobs.nms.misc.MobHealthPack;
import apple.voltskiya.custom_mobs.nms.nether.angered_soul.MobAngeredSoul;
import apple.voltskiya.custom_mobs.nms.nether.revenant.MobRevenant;
import com.voltskiya.lib.AbstractModule;

public class NmsPlugin extends AbstractModule {

    @Override
    public void enable() {
        MobHealthPack.spawner().init();
        MobAngeredSoul.spawner().init();
        MobRevenant.spawner().init();
    }

    @Override
    public String getName() {
        return "Nms";
    }
}
