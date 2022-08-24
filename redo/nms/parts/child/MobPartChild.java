package apple.voltskiya.custom_mobs.mobs.nms.parts.child;

import apple.nms.decoding.iregistry.DecodeDamageSource;
import apple.voltskiya.custom_mobs.mobs.nms.parts.NmsModelEntityConfig;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public interface MobPartChild {
    Packet<?> moveFromMother(boolean isLookingRelevant);

    void a(DamageSource damageSource);

    Entity getSelfEntity();

    NmsModelEntityConfig getConfig();

    default void die() {
        a(DecodeDamageSource.OUT_OF_WORLD);
    }
}
