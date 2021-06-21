package apple.voltskiya.custom_mobs.pathfinders.spell;

import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.util.DistanceUtils;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import org.bukkit.Bukkit;

public class PathfinderGoalShootSpell<Caster extends PathfinderGoalShootSpell.SpellCaster> extends PathfinderGoal {
    protected final EntityInsentient me;
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
    public boolean a() {
        return this.me.isAlive() &&
                DecodeEntity.getTicksLived(me) - lastShot >= type.getCooldown() &&
                this.me.getGoalTarget() != null &&
                type.inRange(DistanceUtils.distance(
                        this.me.getGoalTarget().getBukkitEntity().getLocation(),
                        this.me.getBukkitEntity().getLocation()
                ));
    }

    /**
     * always return false because we shouldn't run more than once
     *
     * @return true if we should keep running. otherwise false
     */
    @Override
    public boolean b() {
        return false;
    }

    /**
     * start the pathfinding
     */
    @Override
    public void c() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), type.construct(spellCaster)::stateChoice);
        this.lastShot = DecodeEntity.getTicksLived(me);
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
        protected EntityInsentient me;

        public SpellCaster(EntityInsentient me) {
            this.me = me;
        }

        public EntityInsentient getEntity() {
            return this.me;
        }
    }
}
