package apple.voltskiya.custom_mobs.turrets;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import static apple.voltskiya.custom_mobs.old_turrets.OldTurretMob.TURRET_TAG;

public class TurretListener implements Listener {
    public TurretListener() {
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        Entity rightClicked = event.getRightClicked();
        if (TurretList.interact(event.getPlayer(), rightClicked)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        if (entity.getScoreboardTags().contains(TURRET_TAG)) {
            TurretList.damage(event.getDamage(), entity);
        }
    }
}
