package apple.voltskiya.custom_mobs.mobs.parts;

import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.WorldServer;

public interface MobPartChild {
    Packet<?> moveFromMother(boolean isLookingRelevant);
    void die();
    Entity getThisEntity();
    MobPartChild remake(WorldServer worldserver, MobPartMother mobParasite);
}
