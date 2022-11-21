package apple.voltskiya.custom_mobs.abilities.nether.fire_fangs;

import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.listen.SpawnListenerHolder;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.ability.MMAbilityConfig;
import apple.voltskiya.mob_manager.mob.ability.activation.Activation;
import apple.voltskiya.mob_manager.mob.ability.activation.ActivationRange;
import java.util.Collection;
import java.util.List;

public class FireFangsSpawner implements SpawnListenerHolder {

    public FireFangsTypeConfig basic = new FireFangsTypeConfig(false, false, false);
    public FireFangsTypeConfig triple = new FireFangsTypeConfig(false, true, false);
    public FireFangsTypeConfig tripleStraight = new FireFangsTypeConfig(true, true, false);
    public FireFangsTypeConfig blueBasic = new FireFangsTypeConfig(false, false, true);
    public FireFangsTypeConfig blueTriple = new FireFangsTypeConfig(false, true, true);
    public FireFangsTypeConfig blueTripleStraight = new FireFangsTypeConfig(false, true, true);

    @Override
    public Collection<SpawnListener> getListeners() {
        return List.of(basic, triple, tripleStraight, blueBasic, blueTriple, blueTripleStraight);
    }

    public static class FireFangsTypeConfig extends MMAbilityConfig {

        public double step = 1;
        public int fireLength = 250;
        public transient boolean isBlue;
        public transient boolean isTriple;
        public transient boolean isStraight;

        public ActivationRange range = new ActivationRange(20);
        private transient String tag = "fire_fangs";

        public FireFangsTypeConfig() {
        }

        public FireFangsTypeConfig(boolean isStraight, boolean isTriple, boolean isBlue) {
            if (!isStraight && !isTriple)
                this.tag += ".basic";

            this.isTriple = isTriple;
            if (this.isTriple)
                this.tag += ".triple";

            this.isStraight = isStraight;
            if (isStraight)
                this.tag += ".straight";

            this.isBlue = isBlue;
            if (this.isBlue)
                this.tag += ".blue";
        }


        @Override
        public void doSpawn(MMSpawned mob) {
            if (this.isStraight)
                new FireFangsSpellStraight(mob, this);
            else
                new FireFangsSpell(mob, this);
        }

        @Override
        public String getBriefTag() {
            return this.tag;
        }

        @Override
        public Collection<Activation> getActivations() {
            return List.of(range);
        }
    }
}
