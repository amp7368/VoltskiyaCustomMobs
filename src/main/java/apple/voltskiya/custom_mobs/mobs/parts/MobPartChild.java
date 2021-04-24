package apple.voltskiya.custom_mobs.mobs.parts;

import net.minecraft.server.v1_16_R3.Packet;

public interface MobPartChild {
    Packet<?> moveFromMother(boolean isLookingRelevant);
    void die();
}
