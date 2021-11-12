package apple.voltskiya.custom_mobs.mobs.nms.overworld.apc33.config;

import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobConfigHolder;
import ycm.yml.manager.fields.YcmField;

import java.util.Collection;
import java.util.List;

public class MobAPC33ConfigHolder implements NmsMobConfigHolder<MobAPC33Config<?, ?>> {
    private static MobAPC33ConfigHolder instance;
    @YcmField
    public MobAPC33ConfigWhole mob = new MobAPC33ConfigWhole();
    @YcmField
    public MobAPC33ConfigLargeCannon cannon = new MobAPC33ConfigLargeCannon();
    @YcmField
    public MobAPC33ConfigSmallGun sideGun = new MobAPC33ConfigSmallGun();
    @YcmField
    public MobAPC33ConfigTreads treads = new MobAPC33ConfigTreads();

    public MobAPC33ConfigHolder() {
        instance = this;
    }

    public static MobAPC33ConfigHolder get() {
        return instance;
    }

    @Override
    public Collection<? extends MobAPC33Config<?, ?>> getConfigurations() {
        return List.of(mob, cannon, sideGun, treads);
    }
}
