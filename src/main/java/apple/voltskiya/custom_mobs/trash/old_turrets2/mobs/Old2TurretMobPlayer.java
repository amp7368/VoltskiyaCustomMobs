package apple.voltskiya.custom_mobs.trash.old_turrets2.mobs;

import apple.voltskiya.custom_mobs.trash.old_turrets2.Old2TurretMobSaveable;
import apple.voltskiya.custom_mobs.trash.old_turrets2.Old2TurretType;
import apple.voltskiya.custom_mobs.trash.old_turrets2.gui.OldTurretGuiPlayer;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.utilities.util.gui.InventoryGui;

public class Old2TurretMobPlayer extends Old2TurretMob {
    private OldTurretGuiPlayer turretGui = null;
    private Old2TargetingMode targetingMode;

    public Old2TurretMobPlayer(Player player) {
        super(Old2TurretType.PLAYER.getUsername());
        targetingMode = Old2TargetingMode.ALL;
    }

    public Old2TurretMobPlayer(Old2TurretMobPlayerSaveable turretMobPlayerSaveable) {
        super(Old2TurretType.PLAYER.getUsername(), turretMobPlayerSaveable);
        targetingMode = turretMobPlayerSaveable.getTargetingMode();
    }

    @Override
    public Old2TurretMobSaveable toSaveable() {
        return new Old2TurretMobPlayerSaveable(this);
    }

    @Override
    protected InventoryGui getTurretGui(Player player) {
        if (turretGui == null)
            turretGui = new OldTurretGuiPlayer(this);
        return turretGui;
    }

    @Override
    protected void updateGui() {
        if (turretGui != null) {
            if (turretGui.getInventory().getViewers().isEmpty()) turretGui = null;
            else turretGui.update();
        }
    }

    public Old2TargetingMode getMode() {
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

    public static class Old2TurretMobPlayerSaveable extends Old2TurretMobSaveable {
        private String uuid;
        private Old2TargetingMode targetingMode;

        public Old2TurretMobPlayerSaveable(Old2TurretMobPlayer turretMobPlayer) {
            super(turretMobPlayer);
            this.uuid = turretMobPlayer.getUUID();
            this.targetingMode = turretMobPlayer.getMode();
        }

        public Old2TurretMobPlayerSaveable() {
        }

        @Override
        public Old2TurretMob build() {
            return new Old2TurretMobPlayer(this);
        }

        @Override
        public String getUUID() {
            return uuid;
        }

        public Old2TargetingMode getTargetingMode() {
            return targetingMode;
        }
    }
}
