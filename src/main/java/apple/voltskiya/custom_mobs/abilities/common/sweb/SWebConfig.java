package apple.voltskiya.custom_mobs.abilities.common.sweb;

import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.listen.SpawnListenerHolder;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.ability.MMAbilityConfig;
import java.util.Collection;
import java.util.List;

public class SWebConfig extends MMAbilityConfig implements SpawnListenerHolder {

    public static String SWEB_WEB_TAG = "internal.sweb.spider_web_block";
    public static String SWEB_IGNORE_PASSIVE = "passive.sweb.ignore_effect";

    public double slowJump = 0.5;
    public double slowMove = 0.4;
    public int spellDuration = 5 * 20;
    public double projectileVelocity = 8;
    public double maxProjectileDistance = 16;

    @Override
    public void doSpawn(MMSpawned mmSpawned) {
        new SWebMob(mmSpawned, this);
    }

    @Override
    public String getBriefTag() {
        return "sweb";
    }

    @Override
    public Collection<SpawnListener> getListeners() {
        return List.of(this);
    }
}
