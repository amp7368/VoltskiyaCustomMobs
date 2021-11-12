package apple.voltskiya.custom_mobs.mobs.nms.overworld.apc33.register;

import apple.voltskiya.custom_mobs.mobs.nms.overworld.apc33.config.MobAPC33Config;
import apple.voltskiya.custom_mobs.mobs.nms.overworld.apc33.config.MobAPC33ConfigHolder;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobConfigHolder;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobEntityEater;

public class MobAPC33EntityEater extends NmsMobEntityEater<MobAPC33Config<?, ?>> {
    @Override
    public Class<? extends NmsMobConfigHolder<MobAPC33Config<?, ?>>> getConfigClass() {
        return MobAPC33ConfigHolder.class;
    }

    @Override
    public String getParentName() {
        return "apc33";
    }
}
