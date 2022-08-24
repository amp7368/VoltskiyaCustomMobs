package apple.voltskiya.custom_mobs.mobs.nms.cool.aledar.config;

import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobConfigHolder;


import java.util.Collection;
import java.util.List;

public class MobAledarConfigHolder implements NmsMobConfigHolder<MobAledarConfig<?, ?>> {
    
    public MobAledarConfigImpl mob = new MobAledarConfigImpl();

    @Override
    public Collection<? extends MobAledarConfigImpl> getConfigurations() {
        return List.of(mob);
    }
}
