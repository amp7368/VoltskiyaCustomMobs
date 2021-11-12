package apple.voltskiya.custom_mobs.mobs.nms.overworld.apc33.config;

import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMob;
import apple.voltskiya.custom_mobs.mobs.nms.parent.holder.NmsMobConfig;
import net.minecraft.world.entity.Entity;

public abstract class MobAPC33Config<
        TypeEntity extends Entity & NmsMob<TypeEntity, Config>,
        Config extends NmsMobConfig<TypeEntity, Config>
        > implements NmsMobConfig<TypeEntity, Config> {
}
