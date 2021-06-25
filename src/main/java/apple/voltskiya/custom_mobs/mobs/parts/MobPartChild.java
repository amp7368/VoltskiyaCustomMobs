package apple.voltskiya.custom_mobs.mobs.parts;

import apple.voltskiya.custom_mobs.mobs.RegisteredCustomMob;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;

public interface MobPartChild extends RegisteredCustomMob {
    Packet<?> moveFromMother(boolean isLookingRelevant);

    void die();

    Entity getThisEntity();
}
