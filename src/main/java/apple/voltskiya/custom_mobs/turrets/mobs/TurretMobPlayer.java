package apple.voltskiya.custom_mobs.turrets.mobs;

import apple.voltskiya.custom_mobs.turrets.TurretMobSaveable;
import apple.voltskiya.custom_mobs.turrets.TurretType;
import apple.voltskiya.custom_mobs.turrets.gui.TurretGuiPlayer;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.utilities.util.gui.InventoryGui;

public class TurretMobPlayer extends TurretMob {
    private TurretGuiPlayer turretGui = null;
    private TargetingMode targetingMode;

    public TurretMobPlayer(Player player) {
        super(TurretType.PLAYER.getUsername());
        targetingMode = TargetingMode.ALL;
    }

    public TurretMobPlayer(TurretMobPlayerSaveable turretMobPlayerSaveable) {
        super(TurretType.PLAYER.getUsername(), turretMobPlayerSaveable);
        targetingMode = turretMobPlayerSaveable.getTargetingMode();
    }

    @Override
    public TurretMobSaveable toSaveable() {
        return new TurretMobPlayerSaveable(this);
    }

    @Override
    protected InventoryGui getTurretGui(Player player) {
        if (turretGui == null)
            turretGui = new TurretGuiPlayer(this);
        return turretGui;
    }

    @Override
    protected void updateGui() {
        if (turretGui != null) {
            if (turretGui.getInventory().getViewers().isEmpty()) turretGui = null;
            else turretGui.update();
        }
    }

    public TargetingMode getMode() {
        return targetingMode;
    }

    public void cycleMode() {
        targetingMode = targetingMode.cycle();
        updateGui();
    }

    @Override
    protected boolean shouldTarget(@Nullable Entity entity) {
        if (entity != null && entity.getScoreboardTags().contains(TURRET_TAG)) return false;
        if (entity instanceof Player player) {
            return player.getGameMode() == GameMode.SURVIVAL && targetingMode.targetsPlayers() && super.shouldTarget(entity);
        } else {
            return targetingMode.targetsMobs() && super.shouldTarget(entity);
        }
    }

    public static class TurretMobPlayerSaveable extends TurretMobSaveable {
        private String uuid;
        private TargetingMode targetingMode;

        public TurretMobPlayerSaveable(TurretMobPlayer turretMobPlayer) {
            super(turretMobPlayer);
            this.uuid = turretMobPlayer.getUUID();
            this.targetingMode = turretMobPlayer.getMode();
        }

        public TurretMobPlayerSaveable() {
        }

        @Override
        public TurretMob build() {
            return new TurretMobPlayer(this);
        }

        @Override
        public String getUUID() {
            return uuid;
        }

        public TargetingMode getTargetingMode() {
            return targetingMode;
        }
    }
}
