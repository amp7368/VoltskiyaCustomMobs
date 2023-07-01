package apple.voltskiya.custom_mobs.pathfinders.spell;

import apple.mc.utilities.world.vector.VectorUtils;
import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;

public class GoalShootSpell<Caster extends GoalShootSpell.SpellCaster> extends
    Goal {

    protected final Mob me;
    protected final SpellType<Caster> type;
    protected final Caster spellCaster;
    protected int lastShot;

    public GoalShootSpell(Caster spellCaster, SpellType<Caster> type) {
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
        CraftEntity bukkitEntity = this.me.getBukkitEntity();
        EntityLiving lastTarget = DecodeEntity.getLastTarget(me);
        return bukkitEntity.isDead()
            && DecodeEntity.getTicksLived(me) - lastShot >= type.getCooldown() && lastTarget != null
            && type.inRange(VectorUtils.distance(lastTarget.getBukkitEntity().getLocation(),
            bukkitEntity.getLocation()));
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
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(),
            type.construct(spellCaster)::stateChoice);
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

        protected Mob me;

        public SpellCaster(Mob me) {
            this.me = me;
        }

        public Mob getEntity() {
            return this.me;
        }
    }
}
