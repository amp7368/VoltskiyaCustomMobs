package apple.voltskiya.custom_mobs.abilities.nether.fire_fangs;

import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.listen.SpawnListenerHolder;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.ability.MMAbilityConfig;
import java.util.Collection;
import java.util.List;

public class FireFangsSpawner implements SpawnListenerHolder {

    public FireFangsTypeConfig basic = new FireFangsTypeConfig();
    public FireFangsTypeConfig triple = new FireFangsTypeConfig();
    public FireFangsTypeConfig tripleStraight = new FireFangsTypeConfig();
    public FireFangsTypeConfig blueBasic = new FireFangsTypeConfig();
    public FireFangsTypeConfig blueTriple = new FireFangsTypeConfig();
    public FireFangsTypeConfig blueTripleStraight = new FireFangsTypeConfig();

    @Override
    public Collection<SpawnListener> getListeners() {
        basic.tag = "fire_fangs.basic.blue";
        triple.tag = "fire_fangs.triple.blue";
        tripleStraight.tag = "fire_fangs.triple_straight.blue";
        blueBasic.tag = "fire_fangs.basic.blue";
        blueTriple.tag = "fire_fangs.triple.blue";
        blueTripleStraight.tag = "fire_fangs.triple_straight.blue";

        triple.isTriple = true;
        tripleStraight.isTriple = true;
        blueTriple.isTriple = true;
        blueTripleStraight.isTriple = true;

        blueBasic.isBlue = true;
        blueTriple.isBlue = true;
        blueTripleStraight.isBlue = true;

        tripleStraight.isStraight = true;
        blueTripleStraight.isStraight = true;

        return List.of(basic, triple, tripleStraight, blueBasic, blueTriple, blueTripleStraight);
    }

    public static class FireFangsTypeConfig extends MMAbilityConfig {

        public double step = 1;
        public int fireLength = 250;
        public boolean isBlue;
        public boolean isTriple;
        private String tag;
        private boolean isStraight = false;


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
    }
}
