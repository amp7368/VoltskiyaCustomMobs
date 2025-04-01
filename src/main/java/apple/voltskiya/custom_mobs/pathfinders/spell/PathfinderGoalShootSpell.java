package apple.voltskiya.custom_mobs.pathfinders.spell;

import apple.mc.utilities.world.vector.VectorUtils;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftEntity;

public class PathfinderGoalShootSpell<Caster extends PathfinderGoalShootSpell.SpellCaster> extends
    Goal {

    protected final Mob me;
    protected final SpellType<Caster> type;
    protected final Caster spellCaster;
    protected int lastShot;

    public PathfinderGoalShootSpell(Caster spellCaster, SpellType<Caster> type) {
        this.me = spellCaster.getEntity();
        this.spellCaster = spellCaster;
        this.type = type;
        this.lastShot = -type.getCooldown();
    }

    /**
     * @return whether this pathfinder should be started
     */
    @Override
    public boolean canUse() {
        CraftEntity bukkitEntity = this.me.getBukkitEntity();
        LivingEntity lastTarget = me.getTarget();
        return !bukkitEntity.isDead()
            && this.me.tickCount - lastShot >= type.getCooldown() && lastTarget != null
            && type.inRange(VectorUtils.distance(lastTarget.getBukkitEntity().getLocation(),
            bukkitEntity.getLocation()));
    }

    /**
     * always return false because we shouldn't run more than once
     *
     * @return true if we should keep running. otherwise false
     */
    @Override
    public boolean canContinueToUse() {
        return false;
    }

    /**
     * start the pathfinding
     */
    @Override
    public void start() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(),
            type.construct(spellCaster)::stateChoice);
        this.lastShot = this.me.tickCount;
    }

    public interface SpellType<Caster extends SpellCaster> {

        int getCooldown();

        boolean inRange(double distance);

        Spell construct(Caster me);
    }

    public interface Spell {

        void stateChoice();
    }

    public static class SpellCaster {

        protected Mob me;

        public SpellCaster(Mob me) {
            this.me = me;
        }

        public Mob getEntity() {
            return this.me;
        }
    }
}
