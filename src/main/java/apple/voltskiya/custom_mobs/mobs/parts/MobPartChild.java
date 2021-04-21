package apple.voltskiya.custom_mobs.mobs.parts;

import net.minecraft.server.v1_16_R3.PacketPlayOutEntityStatus;

public interface MobPartChild {
    PacketPlayOutEntityStatus moveFromMother();
    void die();
}
