package apple.voltskiya.custom_mobs.abilities.ai_changes.flamethrower;

import apple.voltskiya.custom_mobs.pathfinders.spell.PathfinderGoalShootSpell;
import net.minecraft.world.entity.Mob;

public class FlameThrowerCaster extends PathfinderGoalShootSpell.SpellCaster {
    public FlameThrowerCaster(Mob me) {
        super(me);
    }
}
