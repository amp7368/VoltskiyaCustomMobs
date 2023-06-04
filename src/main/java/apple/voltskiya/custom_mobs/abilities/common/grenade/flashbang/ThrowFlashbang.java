package apple.voltskiya.custom_mobs.abilities.common.grenade.flashbang;

import apple.utilities.structures.Pair;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.abilities.common.grenade.GrenadeEntityImpact;
import apple.voltskiya.custom_mobs.abilities.common.grenade.ThrowGrenade;
import apple.voltskiya.custom_mobs.sound.PlaySound;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import voltskiya.apple.utilities.action.ActionMeta;
import voltskiya.apple.utilities.action.ActionReturn;
import voltskiya.apple.utilities.action.RepeatableActionImpl;
import voltskiya.apple.utilities.action.RepeatingActionManager;
import voltskiya.apple.utilities.chance.ChanceShapes;
import voltskiya.apple.utilities.minecraft.player.PlayerUtils;
import voltskiya.apple.utilities.particle.ParticleManager;

public class ThrowFlashbang extends ThrowGrenade<FlashbangConfig> implements ParticleManager {

    private final ChanceShapes random = new ChanceShapes();
    private final List<Pair<Player, Integer>> players = new ArrayList<>();
    private final RepeatingActionManager actionManger = new RepeatingActionManager(VoltskiyaPlugin.get())
        .registerAction(new RepeatableActionImpl("sound", this::sound, config.maxBlindnessDuration, 1));
    private Location explodeLocation;

    public ThrowFlashbang(FlashbangConfig config) {
        super(config);
    }

    private ActionReturn sound(ActionMeta meta) {
        for (Pair<Player, Integer> player : players) {
            int ticksLeft = player.getValue() - meta.currentTick();
            if (ticksLeft < 0) continue;
            float volume = 1.2f;
            volume = Math.min(volume, (ticksLeft - (ticksLeft / volume)) * .1f);
            new PlaySound(SoundCategory.PLAYERS, Sound.BLOCK_NOTE_BLOCK_CHIME, volume, 0.7f).play(player.getKey());
        }
        return new ActionReturn(true);
    }

    private void explodeParticle() {
        int spawnParticles = 30;
        double radius = explosionRadius() / 2.5;
        for (int i = 0; i < spawnParticles; i++) {
            Location loc = explodeLocation.clone().add(random.sphere(radius * random.random().nextDouble()));
            explodeLocation.getWorld().spawnParticle(Particle.FLASH, loc, 5);
        }
    }

    @Override
    protected void explode(List<GrenadeEntityImpact> impacts) {
        this.explodeLocation = getLocation();
        this.explodeParticle();
        this.explodeLocation.getWorld().playSound(explodeLocation, Sound.ENTITY_GENERIC_EXPLODE, 1, 2);
        for (GrenadeEntityImpact impact : impacts) {
            flashbangEffect(impact);
        }
        actionManger.start();
    }

    @Override
    protected double explosionRadius() {
        return config.explosionRadius;
    }


    private void flashbangEffect(GrenadeEntityImpact impact) {
        if (!(impact.entity() instanceof LivingEntity entity)) {
            return;
        }
        double hitImpact = impact.hitImpactLog(explosionRadius());
        int duration = (int) (this.config.maxBlindnessDuration * hitImpact);
        if (isFacing(entity)) duration /= 2;

        if (impact.isPlayer()) {
            Player player = impact.getPlayer();
            if (!PlayerUtils.isSurvival(player)) return;
            this.players.add(new Pair<>(player, duration));
            actionManger.startAction("sound");
        } else duration *= 3;
        entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration, 0));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, duration, 0));
    }

    private boolean isFacing(LivingEntity player) {
        Vector difference = this.explodeLocation.subtract(player.getEyeLocation()).toVector();
        Vector direction = player.getEyeLocation().getDirection();
        float angle = difference.angle(direction);
        return Math.toDegrees(angle) > 70;
    }
}
