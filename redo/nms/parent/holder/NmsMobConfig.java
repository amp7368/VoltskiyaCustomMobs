package apple.voltskiya.custom_mobs.nms.parent.holder;

import apple.voltskiya.custom_mobs.nms.parts.NmsModelHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;

public interface NmsMobConfig<TypeEntity extends Entity & NmsMob<TypeEntity, Config>, Config extends NmsMobConfig<TypeEntity, Config>> {
    String getTag();

    @Nullable
    NmsModelHandler.ModelConfigName getModelConfigName();

    NmsMobConstructor<TypeEntity, Config> getEntityBuilder();

    EntityType<?> getReplacement();

    Consumer<NmsMobRegisterConfigable<TypeEntity, Config>> getRegisterPointer();

    Config getSelf();

    default Collection<Consumer<NmsMobRegisterConfigable<TypeEntity, Config>>> getRegisterPointers() {
        return Collections.singleton(getRegisterPointer());
    }

    default NmsMobRegisterConfigable<TypeEntity, Config> make() {
        return new NmsMobRegisterConfigable<>(getSelf());
    }

    default boolean isSpawnable() {
        return true;
    }

    default Function<World, TypeEntity> getEntityBuilderDefaults(NmsMobRegisterConfigable<TypeEntity, Config> register) {
        return (world) -> getEntityBuilder().createNms(register.getEntityType(), world);
    }
}
