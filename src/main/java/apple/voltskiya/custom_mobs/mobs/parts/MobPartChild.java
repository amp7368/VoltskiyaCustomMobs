package apple.voltskiya.custom_mobs.mobs.parts;

import apple.voltskiya.custom_mobs.mobs.RegisteredCustomMob;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.Packet;

public interface MobPartChild extends RegisteredCustomMob {
    Packet<?> moveFromMother(boolean isLookingRelevant);

    void die();

    Entity getThisEntity();
}
