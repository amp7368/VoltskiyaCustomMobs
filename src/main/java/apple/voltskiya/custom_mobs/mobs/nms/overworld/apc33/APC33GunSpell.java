package apple.voltskiya.custom_mobs.mobs.nms.overworld.apc33;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.pathfinders.spell.PathfinderGoalShootSpell;
import net.minecraft.world.entity.EntityLiving;
import org.bukkit.Bukkit;
import voltskiya.apple.utilities.util.DistanceUtils;

import javax.annotation.Nullable;

public class APC33GunSpell implements PathfinderGoalShootSpell.Spell {
    private final MobAPC33SpellCaster spellCaster;
    private final MobAPC33Config.MobAPCMachineGunType spellType;
    private State state;
    private int tick = 1;

    public APC33GunSpell(MobAPC33SpellCaster spellCaster, MobAPC33Config.MobAPCMachineGunType spellType) {
        this.spellCaster = spellCaster;
        this.spellType = spellType;
    }

    @Override
    public void stateChoice() {

        if (state == State.FIRE) {
            fire();
        }
    }

    private void fire() {
        if (tick % spellType.getMachineGunInterval() == 0)
            System.out.println("machine gun");
        if (tick % spellType.getCannonGunInterval() == 0)
            System.out.println("cannon gun");

        @Nullable EntityLiving goalTarget = spellCaster.getEntity().getGoalTarget();
        if (goalTarget != null && spellType.inRange(DistanceUtils.distance(
                spellCaster.getEntity().getBukkitEntity().getLocation(),
                goalTarget.getBukkitEntity().getLocation()
        ))) {
            state = State.FIRE;
        } else {
            state = State.COMPLETE;
        }
        this.tick++;
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this::stateChoice, 1);
    }

    private enum State {
        FIRE,
        COMPLETE
    }
}
