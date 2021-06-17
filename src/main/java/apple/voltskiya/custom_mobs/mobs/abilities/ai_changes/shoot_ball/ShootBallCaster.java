package apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.shoot_ball;

import apple.voltskiya.custom_mobs.pathfinders.spell.PathfinderGoalShootSpell;
import net.minecraft.server.v1_16_R3.EntityInsentient;

public class ShootBallCaster extends PathfinderGoalShootSpell.SpellCaster {
    public ShootBallCaster(EntityInsentient me) {
        super(me);
    }
}
