package apple.voltskiya.custom_mobs.mobs.nms.parts.child;

import apple.voltskiya.custom_mobs.mobs.nms.parent.register.RegisteredCustomMob;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;

public interface MobPartChild extends RegisteredCustomMob {
    Packet<?> moveFromMother(boolean isLookingRelevant);

    void die();

    Entity getThisEntity();
}
