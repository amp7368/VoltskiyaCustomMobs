package apple.voltskiya.custom_mobs.turret.manage;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.turret.gm.TurretMobGm;
import apple.voltskiya.custom_mobs.turret.manage.model.config.TurretModelConfig;
import apple.voltskiya.custom_mobs.turret.manage.model.config.TurretModelHandlerConfig;
import apple.voltskiya.custom_mobs.turret.manage.model.config.TurretModelNames;
import apple.voltskiya.custom_mobs.turret.manage.model.impl.TurretModelImpl;
import apple.voltskiya.custom_mobs.turret.parent.TurretMob;
import apple.voltskiya.custom_mobs.turret.player.TurretMobPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.HashMap;
import java.util.Map;

public class TurretSpawnListener implements Listener {
    private static final Map<String, TurretTickManager> turretTickers = new HashMap<>();

    public TurretSpawnListener() {
        turretTickers.put(TurretTypeIdentifier.PLAYER.getSpawnTag(),
                new TurretTickManager(
                        getModel(TurretModelNames.PLAYER),
                        TurretModelImpl::new,
                        TurretMobPlayer::new)
        );
        turretTickers.put(TurretTypeIdentifier.GM.getSpawnTag(),
                new TurretTickManager(
                        getModel(TurretModelNames.GM),
                        TurretModelImpl::new,
                        TurretMobGm::new)
        );
        turretTickers.put(TurretTypeIdentifier.INFINITE.getSpawnTag(),
                new TurretTickManager(
                        getModel(TurretModelNames.INFINITE),
                        TurretModelImpl::new,
                        TurretMobGm::new
                )
        );
        VoltskiyaPlugin.get().registerEvents(this);
    }

    public static void addToTicker(TurretMob<?> turretMob) {
        TurretTypeIdentifier id = turretMob.getTypeIdentifier();
        turretTickers.get(id.getSpawnTag()).addMobToTick(turretMob);
    }

    public TurretModelConfig getModel(TurretModelNames name) {
        return TurretModelHandlerConfig.get().getModel(name);
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        for (String tag : event.getEntity().getScoreboardTags()) {
            TurretTickManager eater = turretTickers.get(tag);
            if (eater != null) {
                eater.spawnNew(event.getLocation());
                event.setCancelled(true);
                break;
            }
        }
    }
}
