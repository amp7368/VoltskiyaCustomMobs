package apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.flamethrower;

import apple.voltskiya.custom_mobs.pathfinders.spell.PathfinderGoalShootSpell;
import net.minecraft.server.v1_16_R3.EntityInsentient;

public class FlameThrowerCaster extends PathfinderGoalShootSpell.SpellCaster {
    public FlameThrowerCaster(EntityInsentient me) {
        super(me);
    }
}
