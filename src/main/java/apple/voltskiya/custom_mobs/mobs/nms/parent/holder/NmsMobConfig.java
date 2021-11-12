package apple.voltskiya.custom_mobs.mobs.nms.parent.holder;

import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModelConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;

public interface NmsMobConfig<TypeEntity extends Entity & NmsMob<TypeEntity, Config>, Config extends NmsMobConfig<TypeEntity, Config>> {
    String getTag();

    @Nullable
    NmsModelConfig.ModelConfigName getModelConfigName();

    NmsMobConstructor<TypeEntity, Config> getEntityBuilder();

    EntityTypes<?> getReplacement();

    Consumer<NmsMobRegister<TypeEntity, Config>> getRegisterPointer();

    Config getSelf();

    default Collection<Consumer<NmsMobRegister<TypeEntity, Config>>> getRegisterPointers() {
        return Collections.singleton(getRegisterPointer());
    }

    default boolean hasModel() {
        return getModelConfigName() != null;
    }

    default NmsMobRegister<TypeEntity, Config> make() {
        return new NmsMobRegister<>(getSelf());
    }

    default boolean isSpawnable() {
        return true;
    }

    default Function<World, TypeEntity> getEntityBuilderDefaults(NmsMobRegister<TypeEntity, Config> register) {
        return (world) -> getEntityBuilder().createNms(register.getEntityType(), world);
    }
}
