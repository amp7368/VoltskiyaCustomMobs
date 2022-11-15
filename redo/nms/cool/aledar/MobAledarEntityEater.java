package apple.voltskiya.custom_mobs.nms.cool.aledar;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.shulker_rain.ShulkerRainConfigHolder;
import apple.voltskiya.custom_mobs.nms.cool.aledar.config.MobAledarConfig;
import apple.voltskiya.custom_mobs.nms.cool.aledar.config.MobAledarConfigHolder;
import apple.voltskiya.custom_mobs.nms.parent.holder.NmsMobConfigHolder;
import apple.voltskiya.custom_mobs.nms.parent.holder.NmsMobEntityEater;

public class MobAledarEntityEater extends NmsMobEntityEater<MobAledarConfig<?, ?>> {
    @Override
    public Class<? extends NmsMobConfigHolder<MobAledarConfig<?, ?>>> getConfigClass() {
        return MobAledarConfigHolder.class;
    }

    @Override
    public String getParentName() {
        return "aledar";
    }
}
