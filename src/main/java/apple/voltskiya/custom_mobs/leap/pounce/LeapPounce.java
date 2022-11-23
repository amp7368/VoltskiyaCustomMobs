package apple.voltskiya.custom_mobs.leap.pounce;

import apple.voltskiya.custom_mobs.leap.parent.Leap;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LeapPounce<Config extends PounceConfig> extends Leap<Config> {

    public LeapPounce(MMSpawned mob, Config config, Location target) {
        super(mob, config, target);
    }

    @Override
    protected boolean isFinished() {
        return super.isFinished() || this.checkPounce();
    }

    @Override
    public void onAnyFinish() {
        super.onAnyFinish();
        this.checkPounce();
    }

    private boolean checkPounce() {
        List<Entity> nearby = getEntity().getNearbyEntities(1, 2, 1);
        boolean pounce = false;
        for (Entity entity : nearby) {
            if (entity instanceof Player living) {
                pounceStun(living);
                pounce = true;
            }
        }
        return pounce;
    }

    private void pounceStun(LivingEntity toStun) {
        toStun.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, fullConfig.stunTime, 7, true));
    }
}
