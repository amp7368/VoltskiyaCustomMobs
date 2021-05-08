package apple.voltskiya.custom_mobs.abilities.ai_changes.flamethrower;

import apple.voltskiya.custom_mobs.pathfinders.spell.PathfinderGoalShootSpell;
import net.minecraft.server.v1_16_R3.EntityInsentient;

public class FlameThrowerSpell implements PathfinderGoalShootSpell.Spell {
    private EntityInsentient me;
    private FlameThrower.FlamethrowerType flamethrowerType;

    public FlameThrowerSpell(FlameThrowerCaster me, FlameThrower.FlamethrowerType type) {
        this.me = me.getEntity();
        this.flamethrowerType = type;
    }

    @Override
    public void stateChoice() {

    }
}
