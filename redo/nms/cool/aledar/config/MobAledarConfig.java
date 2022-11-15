package apple.voltskiya.custom_mobs.nms.cool.aledar.config;

import apple.voltskiya.custom_mobs.nms.parent.holder.NmsMob;
import apple.voltskiya.custom_mobs.nms.parent.holder.NmsMobConfig;
import net.minecraft.world.entity.Entity;

public abstract class MobAledarConfig<
        TypeEntity extends Entity & NmsMob<TypeEntity, Config>,
        Config extends NmsMobConfig<TypeEntity, Config>
        > implements NmsMobConfig<TypeEntity, Config> {
}
